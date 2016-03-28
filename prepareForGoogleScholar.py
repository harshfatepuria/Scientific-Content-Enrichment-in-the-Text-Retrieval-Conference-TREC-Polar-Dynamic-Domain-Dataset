import re
from glob import iglob
import os
import sys
import json
import math

inputFilePath=""
inputFileName="grobid_output.json"

input_file2=open(inputFilePath+inputFileName,'r')
teiData = json.loads(input_file2.read())
input_file2.close()

count=0
emptyTitle=0
for files in teiData["TEIAndRelatedData"]:
	
	#print count
	extractedTitle=""
	#use title to get related publications
	if files["TEI"]["teiHeader"]["fileDesc"]["sourceDesc"]["biblStruct"]["analytic"]!="" and files["TEI"]["teiHeader"]["fileDesc"]["sourceDesc"]["biblStruct"]["analytic"].get("title",0)!=0:
		
		if type(files["TEI"]["teiHeader"]["fileDesc"]["sourceDesc"]["biblStruct"]["analytic"]["title"]) is list:
			extractedTitle=files["TEI"]["teiHeader"]["fileDesc"]["sourceDesc"]["biblStruct"]["analytic"]["title"][0]["content"]
	
			removeChars=[]
			for chars in extractedTitle:
				if chars!=',' and chars!=' ' and ( chars<'0' or  (chars>'9' and chars<'A') or (chars>'Z' and chars<'a') or chars>'z'):
					removeChars.append(chars)
			for char in removeChars:
				extractedTitle=extractedTitle.replace(char, "")
			extractedTitle=extractedTitle.replace(" ", ",")
			extractedTitle=extractedTitle.replace(",,", ",")
		
		else:
			extractedTitle=files["TEI"]["teiHeader"]["fileDesc"]["sourceDesc"]["biblStruct"]["analytic"]["title"]["content"]
	
			removeChars=[]
			for chars in extractedTitle:
				if chars!=',' and chars!=' ' and ( chars<'0' or  (chars>'9' and chars<'A') or (chars>'Z' and chars<'a') or chars>'z'):
					removeChars.append(chars)
			for char in removeChars:
				extractedTitle=extractedTitle.replace(char, "")
			extractedTitle=extractedTitle.replace(" ", ",")
			extractedTitle=extractedTitle.replace(",,", ",")
	else:
		emptyTitle=emptyTitle+1
	# print extractedTitle

	teiData["TEIAndRelatedData"][count]["extractedTitle"]=extractedTitle
	# if files["TEI"]["teiHeader"]["fileDesc"]["sourceDesc"]["biblStruct"]["analytic"].get("author",0)!=0:
		

	# 	#use author name to get related publications
	# 	if type(files["TEI"]["teiHeader"]["fileDesc"]["sourceDesc"]["biblStruct"]["analytic"]["author"]) is list:
	# 		#multiple authors
	# 		fullNames=[]
	# 		for names in files["TEI"]["teiHeader"]["fileDesc"]["sourceDesc"]["biblStruct"]["analytic"]["author"]:
	# 			fullName=''

	# 			if names["persName"].get("forename",0)!=0:
	# 				if type(names["persName"]["forename"]) is list:
	# 					for foreNames in names["persName"]["forename"]:
	# 						fullName=fullName+foreNames["content"]+' '
			
	# 				else:
	# 					fullName=fullName+names["persName"]["forename"]["content"]+' '

	# 			if names["persName"].get("surname",0)!=0:		
	# 				fullName=fullName+names["persName"]["surname"]
				
	# 			fullName=fullName.strip()

	# 			print count," ",fullName




	# 	else:
	# 		#one author
	# 		fullName=''

	# 		if files["TEI"]["teiHeader"]["fileDesc"]["sourceDesc"]["biblStruct"]["analytic"]["author"]["persName"].get("forename",0)!=0:
	# 			if type(files["TEI"]["teiHeader"]["fileDesc"]["sourceDesc"]["biblStruct"]["analytic"]["author"]["persName"]["forename"]) is list:
	# 				for foreNames in files["TEI"]["teiHeader"]["fileDesc"]["sourceDesc"]["biblStruct"]["analytic"]["author"]["persName"]["forename"]:
	# 					fullName=fullName+foreNames["content"]+' '
			
	# 			else:
	# 				fullName=fullName+files["TEI"]["teiHeader"]["fileDesc"]["sourceDesc"]["biblStruct"]["analytic"]["author"]["persName"]["forename"]["content"]+' '

	# 		if files["TEI"]["teiHeader"]["fileDesc"]["sourceDesc"]["biblStruct"]["analytic"]["author"]["persName"].get("surname",0)!=0:
	# 			fullName=fullName+files["TEI"]["teiHeader"]["fileDesc"]["sourceDesc"]["biblStruct"]["analytic"]["author"]["persName"]["surname"]
	# 		fullName=fullName.strip()


	# 		print count," ",fullName


	count=count+1
print emptyTitle
keys=json.dumps(teiData, sort_keys=True)
output_file=open(inputFilePath+inputFileName,'w')
output_file.write(keys)
output_file.close()