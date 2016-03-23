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
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryResults;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.util.Repositories;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

public class SweetOntology {

	private static SweetOntology instance;
	private static float defaultTolerance = 0.15f;
	
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
	
	private List<Concept> concepts;
	
	private void loadConcepts() {
		concepts = new ArrayList<>();
		
		List<BindingSet> results = Repositories.tupleQuery(repository, 
	             "SELECT DISTINCT ?s WHERE {{?s ?r ?o} UNION {?o ?r ?s}}", r -> QueryResults.asList(r));
		
		results.forEach(b -> {
			String value = b.getValue("s").stringValue();
			Integer indexOfHash = value.lastIndexOf("#");
			if (indexOfHash < 0) {
				return;
			}
			
			concepts.add(new Concept(value, value.substring(indexOfHash + 1).toLowerCase()));
		});
		
		System.out.println(concepts.size() + " concepts loaded");
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
	}
	
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
