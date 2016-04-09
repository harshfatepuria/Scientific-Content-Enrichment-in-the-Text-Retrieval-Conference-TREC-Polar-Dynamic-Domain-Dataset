GROBID

1. Java file: cs599-content-enrichment/src/main/java/GrobidParsing/GetTEI.java
2. Setup GrobidJournalParser using Tika Server
3. Configure the data set root folder parameters in the GetTEI.java file
4. Run GetTEI.java to generate grobid_output.json which stores TEI information for all the research publications present in the data set (PDF files referenced from fulldump-path-all-json/application%2Fpdf.json).
5. Configure the data set root folder parameters in prepareForGoogleScholar.py
6. Run prepareForGoogleScholar.py to prepare query which is to be used to search related publications.
7. Configure the data set root folder parameters in getRelatedPublications.py
8. Run getRelatedPublications.py to fetch related publications to all the publications present in the data set. Output is a separate JSON for each publication file present in the data set. (Eg. 6EC59B7482A54D3DB9835BC0D21A8B7DA85713524DCEF7AB2005D806127462ED.grobid)




METADATA SCORE ANALYSIS

1. Java file: cs599-content-enrichment/src/main/java/METADATA/MetadataScore.java
2. Configure all folder parameters in MetadataScore.java file.
3. Using files in fulldump-path-all-json folder to reference file paths of different MIME types.
4. Calculating the metadata parameters present in each file using Tika Metadata Content Extraction.
5. Storing the score values grouped by MIME type in metadataScore.json.




TTR AND MEASUREMENT ANALYSIS

1. Java file for TTR analysis: cs599-content-enrichment/src/main/java/TTR/TTRAnalysis.java
2. Java file for measurement analysis (which uses the above TTR analysis): cs599-content-enrichment/src/main/java/measurement/MeasurementParserRunner.java

		java main.Main -t measurement -b baseFolder -r resultFolder [-m markerFile] [-cbor]




GEO TOPIC PARSER

1. Java file: cs599-content-enrichment/src/main/java/geoparser/GeoParserRunner.java

		java main.Main -t geo -b baseFolder -r resultFolder [-m markerFile] [-cbor]




SWEET

1. Java file: cs599-content-enrichment/src/main/java/sweet/SweetParserRunner.java

		java main.Main -t sweet -b baseFolder -r resultFolder [-m markerFile] [-cbor]




RELEVANT SOLR FIELDS USED FOR VISUALIZATIONS AND SEARCH

Please see Schema.xml for the relevant fields used in solr index
1. metadata.Geographic_NAME
2. metadata.measurement_unit
3. metadata.RelatedPublications.0.author
4. metadata.filePath
5. metadata.TEI.teiHeader.fileDesc.titleStmt.title.content
6. metadata.TEI.teiHeader.fileDesc.sourceDesc.biblStruct.analytic.author.persName.forename.content
7. metadata.sweet_concept
8. metadata.shortURL




D3 VISUALIZATIONS (present in the d3 directory)
1. Website for viewing the visualizations: http://harshfatepuria.github.io
2. The following D3 visualizations represent our extracted features and some analysis on them using the parsers
developed:
 
	a. SWEET Concept: sweetOntologyCountBarChart.html
	b. Grobid + Google Scholar: relatedPublicationsDendogram.html
	c. Metadata Score Analysis: metadataScoreCirclePacking.html
	d. Measurement Extraction: measurementCountBarChart.html
	e. Geo Location Analysis: geoLocationCountPieChart.html
 	f. Geo Location Clustering: geoLocationCluster.html
 	g. Tika Similarity: clustering_result/index.html