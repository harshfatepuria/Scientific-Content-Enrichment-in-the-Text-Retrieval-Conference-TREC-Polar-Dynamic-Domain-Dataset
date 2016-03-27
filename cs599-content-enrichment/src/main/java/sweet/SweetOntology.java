package sweet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryResults;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.util.Repositories;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

public class SweetOntology {

	private static SweetOntology instance;
	private static float defaultTolerance = 0.15f;
	private static float defaultMeasurementTolerance = 0.2f;
	
	public static SweetOntology getInstance() throws Exception {
		if (instance == null) {
			instance = new SweetOntology();
		}
		
		return instance;
	}
	
	private Repository repository;
	
	SweetOntology() throws Exception {
		repository = new SailRepository(new MemoryStore());
		repository.initialize();
		loadOntology();
		loadConcepts();
//		loadPrefixs();
		loadMeasurements();
	}
	
	private static String[] ontologyFiles = new String[] { "human.owl", "humanAgriculture.owl", "humanCommerce.owl",
			"humanDecision.owl", "humanEnvirAssessment.owl", "humanEnvirConservation.owl", "humanEnvirControl.owl",
			"humanEnvirStandards.owl", "humanJurisdiction.owl", "humanKnowledgeDomain.owl", "humanResearch.owl",
			"humanTechReadiness.owl", "humanTransportation.owl", "matr.owl", "matrAerosol.owl", "matrAnimal.owl",
			"matrBiomass.owl", "matrCompound.owl", "matrElement.owl", "matrElementalMolecule.owl", "matrEnergy.owl",
			"matrEquipment.owl", "matrFacility.owl", "matrIndustrial.owl", "matrInstrument.owl", "matrIon.owl",
			"matrIsotope.owl", "matrMicrobiota.owl", "matrMineral.owl", "matrNaturalResource.owl",
			"matrOrganicCompound.owl", "matrParticle.owl", "matrPlant.owl", "matrRock.owl", "matrRockIgneous.owl",
			"matrSediment.owl", "matrWater.owl", "phen.owl", "phenAtmo.owl", "phenAtmoCloud.owl", "phenAtmoFog.owl",
			"phenAtmoFront.owl", "phenAtmoLightning.owl", "phenAtmoPrecipitation.owl", "phenAtmoPressure.owl",
			"phenAtmoSky.owl", "phenAtmoTransport.owl", "phenAtmoWind.owl", "phenAtmoWindMesoscale.owl", "phenBiol.owl",
			"phenCryo.owl", "phenCycle.owl", "phenCycleMaterial.owl", "phenEcology.owl", "phenElecMag.owl",
			"phenEnergy.owl", "phenEnvirImpact.owl", "phenFluidDynamics.owl", "phenFluidInstability.owl",
			"phenFluidTransport.owl", "phenGeol.owl", "phenGeolFault.owl", "phenGeolGeomorphology.owl",
			"phenGeolSeismicity.owl", "phenGeolTectonic.owl", "phenGeolVolcano.owl", "phenHelio.owl", "phenHydro.owl",
			"phenMixing.owl", "phenOcean.owl", "phenOceanCoastal.owl", "phenOceanDynamics.owl", "phenPlanetClimate.owl",
			"phenReaction.owl", "phenSolid.owl", "phenStar.owl", "phenSystem.owl", "phenSystemComplexity.owl",
			"phenWave.owl", "phenWaveNoise.owl", "proc.owl", "procChemical.owl", "procPhysical.owl",
			"procStateChange.owl", "procWave.owl", "prop.owl", "propBinary.owl", "propCapacity.owl",
			"propCategorical.owl", "propCharge.owl", "propChemical.owl", "propConductivity.owl", "propCount.owl",
			"propDifference.owl", "propDiffusivity.owl", "propDimensionlessRatio.owl", "propEnergy.owl",
			"propEnergyFlux.owl", "propFraction.owl", "propFunction.owl", "propIndex.owl", "propMass.owl",
			"propMassFlux.owl", "propOrdinal.owl", "propPressure.owl", "propQuantity.owl", "propRotation.owl",
			"propSpace.owl", "propSpaceDirection.owl", "propSpaceDistance.owl", "propSpaceHeight.owl",
			"propSpaceLocation.owl", "propSpaceMultidimensional.owl", "propSpaceThickness.owl", "propSpeed.owl",
			"propTemperature.owl", "propTemperatureGradient.owl", "propTime.owl", "propTimeFrequency.owl", "realm.owl",
			"realmAstroBody.owl", "realmAstroHelio.owl", "realmAstroStar.owl", "realmAtmo.owl",
			"realmAtmoBoundaryLayer.owl", "realmAtmoWeather.owl", "realmBiolBiome.owl", "realmClimateZone.owl",
			"realmCryo.owl", "realmEarthReference.owl", "realmGeol.owl", "realmGeolBasin.owl",
			"realmGeolConstituent.owl", "realmGeolContinental.owl", "realmGeolOceanic.owl", "realmGeolOrogen.owl",
			"realmHydro.owl", "realmHydroBody.owl", "realmLandAeolian.owl", "realmLandCoastal.owl",
			"realmLandFluvial.owl", "realmLandform.owl", "realmLandGlacial.owl", "realmLandOrographic.owl",
			"realmLandProtected.owl", "realmLandTectonic.owl", "realmLandVolcanic.owl", "realmOcean.owl",
			"realmOceanFeature.owl", "realmOceanFloor.owl", "realmRegion.owl", "realmSoil.owl", "rela.owl",
			"relaChemical.owl", "relaClimate.owl", "relaHuman.owl", "relaMath.owl", "relaPhysical.owl",
			"relaProvenance.owl", "relaSci.owl", "relaSpace.owl", "relaTime.owl", "repr.owl", "reprDataFormat.owl",
			"reprDataModel.owl", "reprDataProduct.owl", "reprDataService.owl", "reprDataServiceAnalysis.owl",
			"reprDataServiceGeospatial.owl", "reprDataServiceReduction.owl", "reprDataServiceValidation.owl",
			"reprMath.owl", "reprMathFunction.owl", "reprMathFunctionOrthogonal.owl", "reprMathGraph.owl",
			"reprMathOperation.owl", "reprMathSolution.owl", "reprMathStatistics.owl", "reprSciComponent.owl",
			"reprSciFunction.owl", "reprSciLaw.owl", "reprSciMethodology.owl", "reprSciModel.owl",
			"reprSciProvenance.owl", "reprSciUnits.owl", "reprSpace.owl", "reprSpaceCoordinate.owl",
			"reprSpaceDirection.owl", "reprSpaceGeometry.owl", "reprSpaceGeometry3D.owl",
			"reprSpaceReferenceSystem.owl", "reprTime.owl", "reprTimeDay.owl", "reprTimeSeason.owl", "state.owl",
			"stateBiological.owl", "stateChemical.owl", "stateDataProcessing.owl", "stateEnergyFlux.owl",
			"stateFluid.owl", "stateOrdinal.owl", "statePhysical.owl", "stateRealm.owl", "stateRole.owl",
			"stateRoleBiological.owl", "stateRoleChemical.owl", "stateRoleGeographic.owl", "stateRoleImpact.owl",
			"stateRoleRepresentative.owl", "stateRoleTrust.owl", "stateSolid.owl", "stateSpace.owl",
			"stateSpaceConfiguration.owl", "stateSpaceScale.owl", "stateSpectralBand.owl", "stateSpectralLine.owl",
			"stateStorm.owl", "stateSystem.owl", "stateThermodynamic.owl", "stateTime.owl", "stateTimeCycle.owl",
			"stateTimeFrequency.owl", "stateTimeGeologic.owl", "stateVisibility.owl", "sweetAll.owl" };
	
	private void loadOntology() throws URISyntaxException, IOException {
		try (RepositoryConnection conn = repository.getConnection()) {
			for(String fileName : ontologyFiles) {
				try(InputStream is = this.getClass().getResourceAsStream("/sweet/ontology/" + fileName);) {
					conn.add(is, "http://sweet.jpl.nasa.gov/2.3", RDFFormat.RDFXML);
				}
			}
		}
		System.out.println("Ontology loaded");
	}
	
	/* Storing all concepts for NER matching */
	
	private List<Concept> concepts;
	
	private void loadConcepts() {
		concepts = new ArrayList<>();
		
		List<BindingSet> results = Repositories.tupleQuery(repository, 
	             "SELECT DISTINCT ?s WHERE {{?s ?r ?o} UNION {?o ?r ?s}}", r -> QueryResults.asList(r));
		
		results.forEach(b -> {
			String value = b.getValue("s").stringValue();
			String queryString = getConceptQuery(value);
			
			if (queryString != null) {
				concepts.add(new Concept(value, queryString));	
			}
		});
		
		System.out.println(concepts.size() + " concepts loaded");
	}
	
	private String getConceptString(String concept) {
		Integer indexOfHash = concept.lastIndexOf("#");
		if (indexOfHash < 0) {
			return null;
		}
		
		return concept.substring(indexOfHash + 1);
	}
	
	/* Fuzzy concept querying */
	private String getConceptQuery(String concept) {
		String conceptString = getConceptString(concept);
		return conceptString == null ? null : conceptString.toLowerCase();
	}
	
	public Optional<MatchedConcept> queryFirst(String query) {
		return queryFirst(query, defaultTolerance);
	}
	
	public Optional<MatchedConcept> queryFirst(String query, Float tolerance) {
		List<MatchedConcept> list = query(query, tolerance);
		
		if (list.size() > 0) {
			return Optional.of(list.get(0));
		} else {
			return Optional.ofNullable(null);
		}
	}
	
	public List<MatchedConcept> query(String query) {
		return query(query, defaultTolerance);
	}
	
	public List<MatchedConcept> query(String query, Float tolerance) {
		List<MatchedConcept> matched = new ArrayList<>();
		concepts.forEach(c -> {
			Float distance = c.getDistanceFromQuery(query.trim().toLowerCase());
			
			if (distance <= tolerance * c.queryName.length()) {
				matched.add(new MatchedConcept(c.fullName, distance));
			}
		});
		
		return matched.stream().sorted((a, b) -> Float.compare(a.distance, b.distance)).collect(Collectors.toList());
	}
	
	public List<BindingSet> queryTriples(String concept) {
		ValueFactory factory = repository.getValueFactory();
		
		try (RepositoryConnection con = repository.getConnection()) {
			TupleQuery query = con.prepareTupleQuery("SELECT ?s ?r ?o WHERE {{?s ?r ?o} FILTER( (str(?s) = str(?c)) || (str(?o) = str(?c)) )}");
			query.setBinding("c", factory.createLiteral(concept));
			
			TupleQueryResult queryResults = query.evaluate();
			return QueryResults.asList(queryResults);
		}
	}
	
	public List<BindingSet> queryConceptThatRecursivelyHasRelationWith(String relation, String concept) {
		ValueFactory factory = repository.getValueFactory();
		
		try (RepositoryConnection con = repository.getConnection()) {
			String nestedRelation = String.format("<%s>*/<%s>", relation, relation);
			TupleQuery query = con.prepareTupleQuery("SELECT ?s ?o WHERE {{?s " + nestedRelation + " ?o} FILTER( (str(?o) = str(?io)) )}");
			query.setBinding("io", factory.createLiteral(concept));
			
			TupleQueryResult queryResults = query.evaluate();
			return QueryResults.asList(queryResults);
		}
	}
	
	public List<BindingSet> queryConceptThatRecursivelyHasRelationWith(String firstRelation, String optionalRelation, String concept) {
		ValueFactory factory = repository.getValueFactory();
		
		try (RepositoryConnection con = repository.getConnection()) {
			String nestedRelation = String.format("<%s>/<%s>*/<%s>", firstRelation, optionalRelation, optionalRelation);
			TupleQuery query = con.prepareTupleQuery("SELECT ?s ?o WHERE {{?s " + nestedRelation + " ?o} FILTER( (str(?o) = str(?io)) )}");
			query.setBinding("io", factory.createLiteral(concept));
			
			TupleQueryResult queryResults = query.evaluate();
			return QueryResults.asList(queryResults);
		}
	}
	
	public List<BindingSet> queryConceptThatHasRelation(String concept, String relation) {
		ValueFactory factory = repository.getValueFactory();
		
		try (RepositoryConnection con = repository.getConnection()) {
			String nestedRelation = String.format("<%s>", relation);
			TupleQuery query = con.prepareTupleQuery("SELECT ?s ?o WHERE {{?s " + nestedRelation + " ?o} FILTER( (str(?s) = str(?is)) )}");
			query.setBinding("is", factory.createLiteral(concept));
			
			TupleQueryResult queryResults = query.evaluate();
			return QueryResults.asList(queryResults);
		}
	}
	
	
	/* Store all Measurement/Unit concepts */
//	private List<Concept> prefixs;
	private List<MeasurementConcept> measurements;
	
	private static String relaType = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	private static String relaSubclass = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
//	private static String relaHasSymbol = "http://sweet.jpl.nasa.gov/2.3/relaSci.owl#hasSymbol";
//	private static String conceptPrefix = "http://sweet.jpl.nasa.gov/2.3/reprSciUnits.owl#Prefix";
	private static String conceptUnit = "http://sweet.jpl.nasa.gov/2.3/reprSciUnits.owl#Unit";
	
	/*
	private void loadPrefixs() {
		prefixs = new ArrayList<>();
		List<BindingSet> result = this.queryConceptThatRecursivelyHasRelationWith(relaType, conceptPrefix);
		
		for(BindingSet b : result) {
			String value = b.getValue("s").stringValue();
			String query = getConceptQuery(value);
			if (query != null) {
				prefixs.add(new Concept(value, query));
			}
		}
		System.out.println(prefixs.size() + " prefixes loaded");
	}
	*/
	
	private void loadMeasurements() {
		measurements = new ArrayList<>();
		
		List<BindingSet> result = this.queryConceptThatRecursivelyHasRelationWith(relaType, relaSubclass, conceptUnit);
		
		for(BindingSet b : result) {
			String value = b.getValue("s").stringValue();
			String query = getConceptQuery(value);
			
			if (query.equals("dimensionlessUnit") || query.equals("normalizedUnit") || query.equals("ratio") || query.equals("volumeRatio")) {
				continue;
			}
			
			if (query != null) {
				List<String> symbolList = new ArrayList<>();
//				List<BindingSet> symbolSet = this.queryConceptThatHasRelation(value, relaHasSymbol);
//				symbolSet.forEach(sb -> symbolList.add(sb.getValue("o").stringValue()));
				
				if (query.equals("degreec")) {
					symbolList.add("celsius");
					symbolList.add("degree celsius");
					symbolList.add("degree C");
					symbolList.add("deg C");
					symbolList.add("°C");
				}
				
				if (query.equals("degreef")) {
					symbolList.add("fahrenheit");
					symbolList.add("degree fahrenheit");
					symbolList.add("degree F");
					symbolList.add("deg F");
					symbolList.add("°F");
				}
				
				if (query.equals("kelvin")) {
					symbolList.add("K");
				}
				
				if (query.equals("meter")) {
					symbolList.add("m");
				}
				
				if (query.equals("kilometer")) {
					symbolList.add("km");
				}
				
				if (query.equals("centimeter")) {
					symbolList.add("cm");
				}
				
				if (query.equals("millimeter")) {
					symbolList.add("mm");
				}
				
				if (query.equals("nanometer")) {
					symbolList.add("nm");
				}
				
				if (query.equals("meterSquared")) {
					symbolList.add("square metre");
					symbolList.add("square meter");
					symbolList.add("squaremetre");
					symbolList.add("squaremeter");
				}
				
				if (query.equals("meterCubed")) {
					symbolList.add("cubic metre");
					symbolList.add("cubic meter");
					symbolList.add("cubicmetre");
					symbolList.add("cubicmeter");
				}
				
				if (query.equals("perSecond")) {
					symbolList.add("Hz");
				}
				
				if (query.equals("kilohertz")) {
					symbolList.add("KHz");
				}
				
				if (query.equals("megahertz")) {
					symbolList.add("MHz");
				}
				
				if (query.equals("gigahertz")) {
					symbolList.add("GHz");
				}
				
				if (query.equals("mole")) {
					symbolList.add("mol");
				}
				
				if (query.equals("joule")) {
					symbolList.add("J");
				}
				
				if (query.equals("pascal")) {
					symbolList.add("Pa");
				}
				
				if (query.equals("pascalPerSecond")) {
					symbolList.add("Pa/s");
				}
				
				/*
				if (query.equals("farad")) {
					symbolList.remove("F");
				}
				
				if (query.equals("coulomb")) {
					symbolList.remove("C");
				}
				
				if (query.equals("ampere")) {
					symbolList.remove("a");
				}
				
				if (query.equals("steradian")) {
					symbolList.remove("sr");
				}
				
				if (query.equals("second")) {
					symbolList.remove("s");
				}
				
				if (query.equals("newton")) {
					symbolList.remove("N");
				}
				
				if (query.equals("siemens")) {
					symbolList.remove("G");
				}
				*/
		
				measurements.add(new MeasurementConcept(value, query, symbolList));
			}
		}
		
		System.out.println(measurements.size() + " measurements loaded");
	}
	
	public String matchMeasurement(String p1, String p2) {
		p1 = p1.trim();
		p2 = p2.trim();
		
		String p1Stripped = p1.replaceAll("\\.", "");
		String p2Stripped = p2.replaceAll("\\.", "");
		String concatStripped = p1Stripped + " " + p2Stripped;
		
		/* longest symbol exact match */
		MeasurementConcept bestMc = null;
		int longestSymbolLength = -1;
		
		for(MeasurementConcept mc : measurements) {
			for(String symbol : mc.symbols) {
				if(symbol.length() > longestSymbolLength && (p1Stripped.equals(symbol) || p2Stripped.equals(symbol) || concatStripped.equals(symbol))) {
					longestSymbolLength = symbol.length();
					bestMc = mc;
				}
			}
		}
		
		if (bestMc != null) {
			return getConceptString(bestMc.fullName);
		}
		
		/* longest symbol case-insensitive match */
		bestMc = null;
		longestSymbolLength = -1;
		for(MeasurementConcept mc : measurements) {
			for(String symbol : mc.symbols) {
				if(symbol.length() > longestSymbolLength && (p1Stripped.equalsIgnoreCase(symbol) || p2Stripped.equalsIgnoreCase(symbol) || concatStripped.equalsIgnoreCase(symbol))) {
					longestSymbolLength = symbol.length();
					bestMc = mc;
				}
			}
		}
		
		if (bestMc != null) {
			return getConceptString(bestMc.fullName);
		}
		
		String concat = p1 + " " + p2;
		String p1Lower = p1.toLowerCase();
		String p2Lower = p2.toLowerCase();
		String concatLower = p1Lower + " " + p2Lower;
		
		/* longest query case-insensitive containment */
		/*
		bestMc = null;
		int longestQueryMatchLength = -1;
		for(MeasurementConcept mc : measurements) {
			String query = mc.queryName;
			if(query.length() > longestQueryMatchLength && (concatLower.contains(query))) {
				longestQueryMatchLength = query.length();
				bestMc = mc;
			}
		}
		
		if (bestMc != null) {
			return getConceptString(bestMc.fullName);
		}
		*/
		
		
		/* smallest edit distance query match */
		bestMc = null;
		float smallestDistance = Float.MAX_VALUE;
		
		for(MeasurementConcept mc : measurements) {
			float p1Distance = mc.getDistanceFromQuery(p1Lower);
			float p1Tolerance = defaultMeasurementTolerance * Math.max(mc.queryName.length(), p1.length());
			if (p1Distance <= p1Tolerance && p1Distance < smallestDistance) {
				smallestDistance = p1Distance;
				bestMc = mc;
			}
			
			float p2Distance = mc.getDistanceFromQuery(p2Lower);
			float p2Tolerance = defaultMeasurementTolerance * Math.max(mc.queryName.length(), p2.length());
			if (p2Distance <= p2Tolerance && p2Distance < smallestDistance) {
				smallestDistance = p2Distance;
				bestMc = mc;
			}
			
			
			float concatDistance = mc.getDistanceFromQuery(concatLower);
			float concatTolerance = defaultMeasurementTolerance * Math.max(mc.queryName.length(), concat.length());
			if (concatDistance <= concatTolerance && concatDistance < smallestDistance) {
				smallestDistance = concatDistance;
				bestMc = mc;
			}
		}
		
		if (bestMc != null) {
			return getConceptString(bestMc.fullName);
		}

		return null;
	}
	
	public class MatchedConcept {
		public String concept;
		public Float distance;
		
		public MatchedConcept(String c, Float d) {
			concept = c;
			distance = d;
		}
	}
	
	private class Concept {
		public String fullName;
		public String queryName;
		
		Concept(String fullName, String queryName) {
			this.fullName = fullName;
			this.queryName = queryName;
		}
		
		public float getDistanceFromQuery(String query) {
			return StringUtils.getLevenshteinDistance(queryName, query);
		}
		
		@Override
		public String toString() {
			return fullName + " : " + queryName;
		}
	}
	
	private class MeasurementConcept extends Concept {
		public List<String> symbols;
		MeasurementConcept(String fullName, String queryName, List<String> symbols) {
			super(fullName, queryName);
			this.symbols = symbols;
		}
		
		@Override
		public String toString() {
			return fullName + " : " + queryName + " : " +  Arrays.toString(symbols.toArray());
		}
	}
	
	/*
	public class MeasurementAndPrefix {
		public String measurement;
		public String prefix;
		
		public MeasurementAndPrefix(String measurement) {
			this(measurement, null);
		}
		
		public MeasurementAndPrefix(String measurement, String prefix) {
			this.measurement = measurement;
			this.prefix = prefix;
		}
		
		@Override
		public String toString() {
			if (prefix == null) {
				return measurement;
			} else {
				return measurement + " " + prefix;
			}
		}
	}
	*/
	
	public static void printSweetOntologyFiles() throws URISyntaxException, IOException {
		File ontologyFolder = new File(SweetOntology.class.getResource("/sweet/ontology").toURI());
		List<String> files = new ArrayList<>();
		
		Files.walk(ontologyFolder.toPath())
			.filter(Files::isRegularFile)
			.filter(p -> "owl".equals(FilenameUtils.getExtension(p.toString())))
			.forEach(path -> {
				files.add("\"" + path.getFileName().toString() + "\"");
			});
		
		System.out.println(Arrays.toString(files.toArray()));
	}
}
