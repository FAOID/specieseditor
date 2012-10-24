package org.openforis.specieseditor;

import java.util.List;

import javax.servlet.ServletContext;

import org.openforis.collect.manager.SpeciesManager;
import org.openforis.collect.persistence.TaxonDao;
import org.openforis.collect.persistence.TaxonomyDao;
import org.openforis.idm.model.TaxonOccurrence;
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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;


@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class NewSpecies extends SelectorComposer<Window>  {
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
}
