package org.openforis.specieseditor;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.openforis.collect.manager.SpeciesManager;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.persistence.SurveyDao;
import org.openforis.collect.persistence.TaxonDao;
import org.openforis.collect.persistence.TaxonVernacularNameDao;
import org.openforis.collect.persistence.TaxonomyDao;
import org.openforis.idm.metamodel.CodeList;
import org.openforis.idm.metamodel.CodeListItem;
import org.openforis.idm.model.TaxonOccurrence;
import org.openforis.idm.model.species.TaxonVernacularName;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.GenericAutowireComposer;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class GridSpeciesController extends SelectorComposer<Window>  {
	@Wire
    private Button submitButton;    
   
	@Wire
	private Textbox taxonomyName;
	
	@Wire
	private Textbox speciesName;
	
	@Wire
	private Textbox vernacularName;
	
	@Wire
	private Textbox qualifier1Name;
	
	@WireVariable
	private SpeciesManager speciesManager;
	
	@WireVariable
	private SurveyDao surveyDao;
	
	@Wire
	private Grid speciesGrid;
	
	@Wire
	private Listbox provinceListbox;

	@WireVariable
	private TaxonVernacularNameDao taxonVernacularNameDao;

	@Wire
	private Button btnAdd;
	
	@Wire
	private Label lblSpeciesStatus;
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		
		
		List<TaxonOccurrence> species = speciesManager.findByScientificName("mofor_species", "", 10);
		ListModelList listSpecies = new ListModelList(species);
		speciesGrid.setModel(listSpecies);
		speciesGrid.setRowRenderer(new RowRenderer(){
			public void render(Row row, Object data, int arg2) throws Exception {
				TaxonOccurrence taxon = (TaxonOccurrence) data;
				new Label(taxon.getVernacularName()).setParent(row);
				new Label(taxon.getScientificName()).setParent(row);
				new Label("Todo").setParent(row);
				new Label("Edit Delete").setParent(row);
			}
		});
		
		CollectSurvey survey = surveyDao.load("idnfi");
		List<CodeListItem> provinceList = survey.getCodeList("province").getItems();
		System.out.println(provinceList.size());
		ListModelList listProvince = new ListModelList(provinceList);
		provinceListbox.setModel(listProvince);
		provinceListbox.setItemRenderer(new ListitemRenderer(){
			public void render(Listitem listItem, Object data, int arg2) throws Exception {
				final CodeListItem codeList = (CodeListItem) data;
				listItem.setValue(codeList.getCode());
				new Listcell(codeList.getLabels().get(0).getText()).setParent(listItem);
			}
			
		});
		
		
	}
	
	
	@Listen("onClick=#searchButton")
    public void onSearchButton(){
    	String taxonomy = taxonomyName.getText();
    	String species = speciesName.getText();
    	String vernacular = vernacularName.getText();
    	String qualifier1 = qualifier1Name.getText();
    	Messagebox.show("Saving of " + taxonomy + "," + species + ", " + vernacular + "," + qualifier1);
    	
    	List<TaxonOccurrence> speciesList = speciesManager.findByScientificName(taxonomy, species, 1);
    	Messagebox.show(speciesList.size()+"");
    }
	
	@Listen("onClick=#btnAdd")
	public void onBtnAdd(){
		//species first, find it first
		int taxonId = 0;
		List<TaxonOccurrence> speciesList = speciesManager.findByScientificNameByTaxonRank(taxonomyName.getText(), speciesName.getText(), 1, "species");
		if(speciesList.size()==0){
			//deal with inserting species later on
			lblSpeciesStatus.setValue("Non existing species, please insert new one");
			btnAdd.setLabel("Search>>");
		}else{
			//for now deal with existing species first 
			taxonId = speciesList.get(0).getSystemId();
			
			//display species detail : genus, family, species
			lblSpeciesStatus.setValue("Existing species, you may edit these values");
			btnAdd.setLabel("Insert>>");
			/*
			//vernacularname
			TaxonVernacularName entity = new TaxonVernacularName();
			entity.setVernacularName(vernacularName.getText());
			entity.setLanguageCode("id");
			List<String> qualifiers = new ArrayList<String>();
			qualifiers.add(provinceListbox.getSelectedItem().getValue().toString());
			entity.setQualifiers(qualifiers);
			entity.setStep(9);
			entity.setTaxonSystemId(taxonId);
			taxonVernacularNameDao.insert(entity);
			 */
		}
		
		
	}
}
