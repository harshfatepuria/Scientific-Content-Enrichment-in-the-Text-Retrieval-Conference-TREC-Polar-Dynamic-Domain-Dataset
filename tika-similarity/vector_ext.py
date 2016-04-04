#!/usr/bin/env python2.7
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#

import math, editdistance

def stringify(attribute_value):
    if isinstance(attribute_value, list):
        return str((", ".join(isinstance(x, basestring) and x or str(x) for x in attribute_value)).encode('utf-8').strip())
    else:
        return isinstance(attribute_value, basestring) and str(attribute_value.encode('utf-8').strip()) or str(attribute_value)


class Vector:
    '''
    An instance of this class represents a vector in n-dimensional space
    '''
    na_metadata = ["resourceName"]
    
    def __init__(self, filename=None, features=None):
        '''
        Create a vector
        @param metadata features 
        '''
        self.allFeatures = features
        self.features = {}
        self.featuresText = {}
        
        if filename and features:
            self.filename = filename
            self.prepareFeatures(features)
            self.featureSet = frozenset(self.features.keys())
    
    def prepareFeatures(self, features):
        for na in self.na_metadata:
            features.pop(na, None)
            
        for key in features:
            self.features[key] = len(stringify(features[key]))
            self.featuresText[key] = stringify(features[key])
        
    '''
    def __str__(self):        
        vector_str = "( {0} ): \n".format(self.)
        if self.features:
            for key in self.features:
                vector_str += " {1}: {2} \n".format(key, self.features[key])
        return vector_str+"\n"
    '''

    def getMagnitude(self):
        totalMagnitude = 0.0
        for key in self.features:
            totalMagnitude += self.features[key] ** 2
        return math.sqrt(totalMagnitude)


    def dotProduct(self, anotherVector):
        '''
        A = ax+by+cz
        B = mx+ny+oz
        A.B = a*m + b*n + c*o
        '''        
        dot_product = 0.0
        intersect_features = set(self.features) & set(anotherVector.features)
        
        for feature in intersect_features:
            dot_product += self.features[feature] * anotherVector.features[feature]
        return dot_product


    def cosTheta(self, v2):
        '''
        cosTheta = (V1.V2) / (|V1| |V2|)
        cos 0 = 1 implies identical documents
        '''
        div = (self.getMagnitude() * v2.getMagnitude())
        
        if (div == 0):
            return 0
        
        return self.dotProduct(v2) / div

    def cosine_dist(self, v2):
        return 1 - self.cosTheta(v2)

    def euclidean_dist(self, anotherVector):
        '''
        dist = ((x1-x2)^2 + (y1-y2)^2 + (z1-z2)^2)^(0.5)
        '''
        intersect_features = set(self.features) & set(anotherVector.features)

        dist_sum = 0.0
        for feature in intersect_features:
            dist_sum += (self.features[feature] - anotherVector.features[feature]) ** 2

        setA = set(self.features) - intersect_features
        for feature in setA:
            dist_sum += self.features[feature] ** 2

        setB = set(anotherVector.features) - intersect_features
        for feature in setB:
            dist_sum += anotherVector.features[feature] ** 2

        return math.sqrt(dist_sum)
    
    def edit_dist(self, anotherVector):
#         intersect_features = set(self.features.keys()) & set(anotherVector.features.keys())
        intersect_features = self.featureSet.intersection(anotherVector.featureSet)                
        intersect_features = [feature for feature in intersect_features if feature not in self.na_metadata ]
        
        file_edit_distance = 0.0
        count = 0
        for feature in intersect_features:
            file1_feature_value = self.featuresText[feature]
            file2_feature_value = anotherVector.featuresText[feature]
            
            divider = (len(file1_feature_value) if len(file1_feature_value) > len(file2_feature_value) else len(file2_feature_value))
            
            if divider == 0:
                continue
            
            feature_distance = float(editdistance.eval(file1_feature_value, file2_feature_value))/ divider
            file_edit_distance += feature_distance
            count += 1
        
        if count == 0:
            return file_edit_distance
        
        file_edit_distance /= count
        return file_edit_distance
    
    def jaccard_sim(self, anotherVector):
        unionNum = len(self.featureSet.union(anotherVector.featureSet))
        intersectNum = len(self.featureSet.intersection(anotherVector.featureSet))
        return float(intersectNum) / float(unionNum)
    
    def jaccard_dist(self, anotherVector):
        return 1 - self.jaccard_sim(anotherVector)
        
    
    def printFeatures(self):
        print "["
        print "filename : " , self.filename
        for key in self.features:
            print key, " : " , self.features[key]
        print "]"

class GeoVector(Vector):
    def prepareFeatures(self, features):
        self.features["metadata.Geographic_LATITUDE"] = features["metadata.Geographic_LATITUDE"][0]
        self.features["metadata.Geographic_LONGITUDE"] = features["metadata.Geographic_LONGITUDE"][0]

class SweetVector(Vector):
    def prepareFeatures(self, features):
        concepts = features["metadata.sweet_concept"]
        for concept in concepts:
            self.features[concept] = 1
            
class MeasurementVector(Vector):
    def prepareFeatures(self, features):
        values = features["metadata.measurement_value"]
        units = features["metadata.measurement_unit"]
        
        mapping = {}
        for idx, val in enumerate(units):
            if not mapping.has_key(val):
                mapping[val] = []
            mapping[val].append(values[idx])
        
        for key, vals in mapping.iteritems():
            avg = reduce(lambda x, y: x+y, vals) / len(vals)
#             print key, " | ", vals, " | ", avg
            self.features[key] = avg

class GrobidVector(Vector):
    def prepareFeatures(self, features):
        for key in features:
            if (key.startswith("metadata.RelatedPublications") and (key.endswith("title") or key.endswith("author"))) or key.startswith("metadata.TEI.teiHeader.fileDesc.sourceDesc.biblStruct.analytic.author"):
                self.features[key] = len(stringify(features[key]))
                self.featuresText[key] = stringify(features[key])