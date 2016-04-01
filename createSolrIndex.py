import os
from glob import iglob
from subprocess import call

#Change file path below----->
file="C:/Users/rahul/Documents/GitHub/Scientific-Content-Enrichment-in-the-Text-Retrieval-Conference-TREC-Polar-Dynamic-Domain-Dataset/"

for path, subdirs, files in os.walk(file):
    for name in files:
        #print os.path.join(path, name)
        #call(["cat",os.path.join(path, name)])
        call(["post", "-c", "599HW", "-type", "application/json", os.path.join(path, name)])
