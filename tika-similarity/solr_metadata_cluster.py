import urllib2
import json
from vector_ext import GeoVector, SweetVector, MeasurementVector, GrobidVector
import kmeans_ext
import argparse

def getJsonData(url):
    data = urllib2.urlopen(url).read()
    return json.loads(data)

def createVectors(json, vectorType):
    vectors = []
    for doc in json['response']['docs']:
        v = createVector(doc, vectorType)
        if (v is not None):
            vectors.append(v)
    return vectors

def createVector(doc, vectorType):
    v = None
    
    if (vectorType == "geo"):
        v = GeoVector(doc['metadata.filePath'][0], doc)
    elif (vectorType == "sweet"):
        v = SweetVector(doc['metadata.filePath'][0], doc)
    elif (vectorType == "measurement"):
        v = MeasurementVector(doc['metadata.filePath'][0], doc)
    elif (vectorType == "grobid"):
        v = GrobidVector(doc['metadata.filePath'][0], doc)   
    
    return v 

def writeJson(bestCluster, fileName):
    with open(fileName, "w") as jsonF:
        json_data = {}
        clusters = []
        for key in bestCluster[1]:    #clusters

            cluster_Dict = {}
            children = []
            for point in bestCluster[1][key]:
                node = {}
                node["metadata"] = json.dumps(point.allFeatures)
                node["name"] = point.filename.split('/')[-1]
                node["path"] = point.filename
                children.append(node)
                
            cluster_Dict["children"] = children
            cluster_Dict["name"] = "cluster" + str(key)

            clusters.append(cluster_Dict)

        json_data["children"] = clusters
        json_data["name"] = "clusters"
        
        json.dump(json_data, jsonF)


def runSample():
    print "Run sample clustering"
    
    metadataUrl = {
#     "geo" : "http://localhost:8983/solr/polar/select?q=metadata.Geographic_LATITUDE%3A*&rows=500&wt=json&indent=true",
#     "sweet" : "http://localhost:8983/solr/polar/select?q=metadata.sweet_concept%3A*&rows=500&wt=json&indent=true",
#     "measurement" : "http://localhost:8983/solr/polar/select?q=metadata.measurement_unit%3A*&rows=500&wt=json&indent=true",
    "grobid" : "http://localhost:8983/solr/polar/select?q=metadata.TEI.xmlns%3A*&rows=50&wt=json&indent=true"
    }
    
    metadataMeasure = {
    "geo" : 0,
    "sweet" : 3,
    "measurement" : 1,
    "grobid" : 2
    }
    
        
    for key, url in metadataUrl.iteritems():
        print "clustering " + key
        res = getJsonData(url)
        vectors = createVectors(res, key)
    
        diffraction = []
        numPoint = []
        
        for k in range(2,11):
            cluster = kmeans_ext.K_Means_iter(vectors, metadataMeasure[key], k)
            writeJson(cluster, "clusters_" + key + "_" + str(k) + ".json")
            diffraction.append((k, cluster[0]))
            
            np = []
            for i, points in cluster[1].iteritems():
                np.append(len(points))
            
            numPoint.append(np)
            
        with open("summary_" + key + ".txt", "w") as sumF:
            for tup in diffraction:
                sumF.write(str(tup[0]) + "\t" + str(tup[1]) + "\n")
            sumF.write("\n")
                
            for n in numPoint:
                sumF.write(str(n) + "\n")

if __name__ == "__main__":
    argParser = argparse.ArgumentParser('K-means Clustering of metadata values')
    argParser.add_argument('--sample', action='store_true', help='Run sample clustering')
    argParser.add_argument('--url', help='URL to query Solr index')
    argParser.add_argument('--type', type=str, help='type of metadata (measurement|grobid|geo|sweet)')
    argParser.add_argument('--measure', type=int, help='0 - Euclidean, 1 - Cosine, 2 - Edit, 3 - Jaccard (default: 0)')
    argParser.add_argument('--k', type=int, help='number of clusters')
    args = argParser.parse_args()
    
    if (args.sample):
        runSample()
    else:
        res = getJsonData(args.url)
        vectors = createVectors(res, args.type)
        
        if args.k is not None:
            cluster = kmeans_ext.K_Means_iter(vectors, args.measure, args.k)
        else:
            cluster = kmeans_ext.K_Means_iter(vectors, args.measure)
        
        writeJson(cluster, "cluster.json")
                    
                    
#         for i, points in cluster[1].iteritems():
#             print "cluster ", i , " : ", len(points)
#             for p in points:
#                 print p.features["metadata.Geographic_LATITUDE"] , "\t" , p.features["metadata.Geographic_LONGITUDE"]



# print vectors[0].features
# for i in range(0, len(vectors)):
#     print vectors[0].jaccard_dist(vectors[i]), " | ", vectors[0].featureSet, " | " , vectors[i].featureSet
#     print vectors[0].cosine_dist(vectors[i]), " | ",  vectors[i].features
#     print vectors[0].edit_dist(vectors[i])
    
# for v in vectors:
#     v.printFeatures()