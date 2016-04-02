# WARNING: THIS PROGRAM WILL CREATE AROUND 1.7 MILLION JSON FILES
# ESTIMATED RUNNING TIME: 24 Hrs
# Create a folder "urlShortnerOutput" where the program is present before proceeding------>

import uuid
import json
import os
from glob import iglob
from pprint import pprint

#Getting JSON file of initial Tika parsing containing list of file paths categorized by MIME types
#Change file path here----->
file="C:/Users/rahul/Documents/GitHub/Scientific-Content-Enrichment-in-the-Text-Retrieval-Conference-TREC-Polar-Dynamic-Domain-Dataset/fulldump-path-all-json/"

for filepath in iglob(os.path.join(file, '*.json')):
    with open(filepath) as data_file:    
        data = json.load(data_file)
        for i in data['files']:
            #Getting a unique md5 hash for the file path relative to the current directory
            d={}
            mapping={}
            mapping["metadata"]={}
            d['filePath']=i
            pathToFile= i.split('/')
            fp=open('urlShortnerOutput/'+pathToFile[-1]+'.json','w')
            s="polar.usc.edu/"+str(uuid.uuid4())[:8]
            d['shortURL']=s
            mapping["metadata"]=d
            #Dumping JSON object with mapped shortened URLs and file path
            keys=json.dumps(d, sort_keys=True)
            fp.write(keys)
            fp.close()
            print "\'"+ i+ "\'" + " : " +"\'"+ s+ "\'"
        data_file.close()
