package org.skyve.impl.web.faces.pipeline.component;

import java.util.List;
import java.util.Map;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.component.html.HtmlOutputLink;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.convert.Converter;

import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.behavior.ajax.AjaxBehaviorListenerImpl;
import org.primefaces.behavior.confirm.ConfirmBehavior;
import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.component.button.Button;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.colorpicker.ColorPicker;
import org.primefaces.component.column.Column;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.component.datalist.DataList;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.editor.Editor;
import org.primefaces.component.graphicimage.GraphicImage;
import org.primefaces.component.inputmask.InputMask;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.inputtextarea.InputTextarea;
import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.password.Password;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.component.selectoneradio.SelectOneRadio;
import org.primefaces.component.spacer.Spacer;
import org.primefaces.component.spinner.Spinner;
import org.primefaces.component.tabview.Tab;
import org.primefaces.component.tabview.TabView;
import org.primefaces.component.toolbar.Toolbar;
import org.skyve.domain.Bean;
import org.skyve.domain.types.converters.Format.TextCase;
import org.skyve.impl.metadata.model.document.field.TextFormat;
import org.skyve.impl.metadata.view.HorizontalAlignment;
import org.skyve.impl.metadata.view.container.TabPane;
import org.skyve.impl.metadata.view.widget.Blurb;
import org.skyve.impl.metadata.view.widget.Link;
import org.skyve.impl.metadata.view.widget.bound.Label;
import org.skyve.impl.metadata.view.widget.bound.input.CheckBox;
import org.skyve.impl.metadata.view.widget.bound.input.ColourPicker;
import org.skyve.impl.metadata.view.widget.bound.input.Combo;
import org.skyve.impl.metadata.view.widget.bound.input.ContentImage;
import org.skyve.impl.metadata.view.widget.bound.input.ContentLink;
import org.skyve.impl.metadata.view.widget.bound.input.HTML;
import org.skyve.impl.metadata.view.widget.bound.input.LookupDescription;
import org.skyve.impl.metadata.view.widget.bound.input.Radio;
import org.skyve.impl.metadata.view.widget.bound.input.RichText;
import org.skyve.impl.metadata.view.widget.bound.input.TextArea;
import org.skyve.impl.metadata.view.widget.bound.input.TextField;
import org.skyve.impl.metadata.view.widget.bound.tabular.DataGrid;
import org.skyve.impl.metadata.view.widget.bound.tabular.DataGridColumn;
import org.skyve.impl.web.AbstractWebContext;
import org.skyve.impl.web.faces.BeanMapAdapter;
import org.skyve.impl.web.faces.converters.select.AssociationAutoCompleteConverter;
import org.skyve.impl.web.faces.converters.select.SelectItemsBeanConverter;
import org.skyve.metadata.controller.ImplicitActionName;
import org.skyve.metadata.model.Attribute.AttributeType;
import org.skyve.metadata.module.query.QueryDefinition;
import org.skyve.metadata.view.widget.bound.Parameter;
import org.skyve.report.ReportFormat;

public class TabularComponentBuilder extends ComponentBuilder {

	@Override
	public HtmlPanelGroup view(String invisibleConditionName) {
		return panelGroup(true, false, false, invisibleConditionName);
	}

	@Override
	public UIComponent toolbar() {
		Toolbar result = (Toolbar) a.createComponent(Toolbar.COMPONENT_TYPE);
		setId(result);
		result.setStyle("width:100%");
		return result;
	}

	@Override
	public UIComponent tabPane(TabPane tabPane) {
		return tabView(tabPane.getInvisibleConditionName());
	}
	
	@Override
	public UIComponent tab(org.skyve.impl.metadata.view.container.Tab tab) {
		Tab result = (Tab) a.createComponent(Tab.COMPONENT_TYPE);
		result.setTitle(tab.getTitle());
		setDisabled(result, tab.getDisabledConditionName());
		setInvisible(result, tab.getInvisibleConditionName(), null);
		setId(result);
		return result;
	}

	@Override
	public UIComponent border(String borderTitle, String invisibleConditionName, Integer pixelWidth) {
		return panel(borderTitle, invisibleConditionName, pixelWidth);
	}
	
	@Override
	public UIComponent label(String value) {
		OutputLabel result = (OutputLabel) a.createComponent(OutputLabel.COMPONENT_TYPE);
		setId(result);
		result.setValue(value);
		return result;
	}
	
	@Override
	public UIComponent actionButton(String listBinding, 
										org.skyve.impl.metadata.view.widget.Button button, 
										org.skyve.metadata.view.Action action) {
		return actionButton(action.getDisplayName(),
				                action.getToolTip(),
				                action.getImplicitName(),
				                action.getName(),
				                false,
				                listBinding,
				                button.getPixelWidth(),
				                button.getPixelHeight(),
				                action.getClientValidation(),
				                action.getConfirmationText(),
				                action.getDisabledConditionName(),
				                action.getInvisibleConditionName());
	}
	
	@Override
	public UIComponent reportButton(org.skyve.impl.metadata.view.widget.Button button, 
									org.skyve.metadata.view.Action action) {
		return reportButton(action.getDisplayName(), 
								action.getToolTip(), 
								action.getParameters(), 
								action.getDisabledConditionName(), 
								action.getInvisibleConditionName());
	}
	
	@Override
	public UIComponent blurb(String value, String binding, Blurb blurb) {
		return text(value, 
						binding, 
						blurb.getTextAlignment(), 
						blurb.getPixelWidth(), 
						blurb.getPixelHeight(), 
						blurb.getInvisibleConditionName(),
						false);
	}
	
	@Override
	public UIComponent label(String value, String binding, Label label) {
		HtmlOutputLabel result = (HtmlOutputLabel) a.createComponent(HtmlOutputLabel.COMPONENT_TYPE);
		setId(result);
		if (value != null) {
			result.setValue(value);
		} 
		else {
			result.setValueExpression("value", createValueExpressionFromBinding(binding, true, null, Object.class));
		}

		return result;
	}

	private int columnPriority;
	
	@Override
	public UIComponent dataGrid(DataGrid grid) {
		columnPriority = 1;
		
		return dataTable(grid.getBinding(),
							grid.getTitle(),
							grid.getInvisibleConditionName(),
							Boolean.TRUE.equals(grid.getInline()));
	}
	
	@Override
	public UIComponent addDataGridBoundColumn(UIComponent current, 
												DataGrid grid,
												DataGridColumn column,
												String columnTitle,
												String columnBinding,
												StringBuilder gridColumnExpression) {
		String gridBinding = grid.getBinding();

		Column result = column(gridBinding,
								(columnBinding == null) ? Bean.BIZ_KEY : columnBinding,
								columnTitle,
	                            column.getAlignment(),
	                            false,
	                            column.getPixelWidth());
		result.setPriority(columnPriority);
		if (columnPriority < 6) {
			columnPriority++;
		}
		current.getChildren().add(result);

		if (! Boolean.TRUE.equals(grid.getInline())) {
	        gridColumnExpression.setLength(0);
	        gridColumnExpression.append('{').append(columnBinding).append('}');
	        result.getChildren().add(outputText(gridBinding, gridColumnExpression.toString()));
		}
		
		return result;
	}
	
	@Override
	public UIComponent addedDataGridBoundColumn(UIComponent current) {
		return current.getParent(); // move from column to table
	}

	@Override
	public UIComponent addDataGridContainerColumn(UIComponent current, DataGrid grid, DataGridColumn column) {
		Column col = column(grid.getBinding(),
								null,
								column.getTitle(),
				                column.getAlignment(),
				                false,
				                column.getPixelWidth());
		col.setPriority(columnPriority);
		if (columnPriority < 6) {
			columnPriority++;
		}
		current.getChildren().add(col);
		return col;
	}
	
	@Override
	public UIComponent addedDataGridContainerColumn(UIComponent current) {
		return current.getParent();
	}
	
	@Override
	public UIComponent addDataGridActionColumn(UIComponent current, 
												DataGrid grid, 
												String gridColumnExpression,
												String singularDocumentAlias,
												boolean inline) {
		String gridBinding = grid.getBinding();
		
		Column col = column(null,
								null,
								"",
				                HorizontalAlignment.centre,
				                true,
				                Integer.valueOf(45));
		col.setPriority(1);

		List<UIComponent> children = col.getChildren();
		// edit action if grid is editable, but grid is not inline
		if ((! Boolean.FALSE.equals(grid.getEditable()) && 
				(! Boolean.TRUE.equals(grid.getInline())))) {
	    	CommandButton button = (CommandButton) a.createComponent(CommandButton.COMPONENT_TYPE);
	    	button.setValue(null);
        	button.setTitle("Add a new " + singularDocumentAlias);
	    	button.setIcon("fa fa-plus");
			action(button, ImplicitActionName.Add, null, gridBinding, inline);
			col.getFacets().put("header", button);

	    	button = (CommandButton) a.createComponent(CommandButton.COMPONENT_TYPE);
	    	button.setValue(null);
        	button.setTitle("Edit this " + singularDocumentAlias);
	    	button.setIcon("fa fa-chevron-right");
			action(button, ImplicitActionName.Navigate, null, gridBinding, inline);
			children.add(button);
		}

		if (! Boolean.FALSE.equals(grid.getEditable())) {
			if (! col.getChildren().isEmpty()) {
				children.add(label(" "));
			}

			CommandButton button = (CommandButton) a.createComponent(CommandButton.COMPONENT_TYPE);
	    	button.setValue(null);
        	button.setTitle("Remove this " + singularDocumentAlias);
	    	button.setIcon("fa fa-minus");
			action(button, ImplicitActionName.Remove, null, gridBinding, true);
			children.add(button);
		}
		
		if (! children.isEmpty()) {
			if (children.size() > 1) {
				col.setStyle("width:90px;text-align:center !important");
			}
			current.getChildren().add(col);
		}
		
		return current;
	}

	@Override
	public UIComponent checkBox(String listBinding, CheckBox checkBox, String title, boolean required) {
/* TODO Why don't tri state checkboxes work???
		UIComponentBase c = Boolean.FALSE.equals(checkBox.getTriState()) ?
								b.checkbox(listBinding,
										checkBox.getBinding(), 
										def.getTitle(),
										def.isRequired(),
										checkBox.getDisabledConditionName()) :
								b.triStateCheckbox(listBinding,
													checkBox.getBinding(), 
													def.getTitle(),
													def.isRequired(),
													checkBox.getDisabledConditionName());
*/
		return checkbox(listBinding,
							checkBox.getBinding(), 
							title,
							required,
							checkBox.getDisabledConditionName());
	}
	
	@Override
	public UIComponent colourPicker(String listBinding, ColourPicker colour, String title, boolean required) {
		return colourPicker(listBinding, 
								colour.getBinding(), 
								title, 
								required, 
								colour.getPixelWidth(),
								true);
	}
	
	@Override
	public UIComponent combo(String listBinding, Combo combo, String title, boolean required) {
		String binding = combo.getBinding();
		HtmlSelectOneMenu s = selectOneMenu(listBinding,
												binding,
								                title,
								                required,
								                combo.getDisabledConditionName(),
								                null);
		UISelectItems i = selectItems(listBinding, binding, true);
		s.getChildren().add(i);
		
		return s;
	}

	@Override
	public UIComponent contentImage(String listBinding, ContentImage image, String title, boolean required) {
		UIComponent result = panelGroup(true, true, false, null);
		result.getChildren().add(contentGraphicImage(image.getPixelWidth(), 
														null, 
														image.getPixelHeight(), 
														null, 
														image.getBinding(), 
														null));
		if (! Boolean.FALSE.equals(image.getEditable())) {
			result.getChildren().add(label("Upload"));
		}
		
		return result;
	}
	
	@Override
	public UIComponent contentLink(String listBinding, ContentLink link, String title, boolean required) {
		String text = link.getValue();
		if (text == null) {
			text = "Content";
		}

		UIComponent result = panelGroup(true, true, false, null);
		result.getChildren().add(contentLink(link.getPixelWidth(), text, link.getBinding()));
		if (! Boolean.FALSE.equals(link.getEditable())) {
			result.getChildren().add(label("Upload"));
		}
		
		return result;
	}
	
	@Override
	public UIComponent html(String listBinding, HTML html, String title, boolean required) {
		return editor(listBinding, html.getBinding(), title, required, html.getDisabledConditionName());
	}
	
	@Override
	public UIComponent lookupDescription(String listBinding, 
											LookupDescription lookup, 
											String title, 
											boolean required,
											String displayBinding,
											QueryDefinition query) {
		return autoComplete(listBinding,
							lookup.getBinding(),
							title,
							required,
							lookup.getDisabledConditionName(),
							displayBinding,
							query,
							lookup.getPixelWidth(),
							false);
	}

	@Override
	public UIComponent password(String listBinding, 
									org.skyve.impl.metadata.view.widget.bound.input.Password password,
									String title, 
									boolean required) {
		return password(listBinding,
							password.getBinding(), 
			                title,
			                required,
			                password.getDisabledConditionName(),
			                password.getPixelWidth(),
			                true);
	}

	@Override
	public UIComponent radio(String listBinding, Radio radio, String title, boolean required) {
		String binding = radio.getBinding();
        UIComponent result = selectOneRadio(listBinding,
												binding,
				                                title,
				                                required,
				                                radio.getDisabledConditionName());
        result.getAttributes().put("binding", radio.getBinding());
        UISelectItems i = selectItems(listBinding, binding, false);
		result.getChildren().add(i);
		return result;
	}
	
	@Override
	public UIComponent richText(String listBinding, RichText text, String title, boolean required) {
        return editor(listBinding, text.getBinding(), title, required, text.getDisabledConditionName());
	}

	@Override
	public UIComponent spinner(String listBinding, 
								org.skyve.impl.metadata.view.widget.bound.input.Spinner spinner,
								String title, 
								boolean required) {
		return spinner(listBinding, 
						spinner.getBinding(), 
						title, 
						required, 
						spinner.getDisabledConditionName(),
						spinner.getPixelWidth());
	}
	
	@Override
	public UIComponent textArea(String listBinding, 
									TextArea text, 
									String title, 
									boolean required,
									Integer length) {
        return textArea(listBinding,
							text.getBinding(),
							title,
							required,
							text.getDisabledConditionName(),
							length,
							text.getPixelWidth(),
							text.getPixelHeight(),
							true);
	}
	
	@Override
	public UIComponent text(String listBinding, 
								TextField text, 
								String title, 
								boolean required,
								Integer length,
								org.skyve.domain.types.converters.Converter<?> converter,
								TextFormat format,
								Converter facesConverter) {
		boolean useCalendar = false;
		if (converter != null) {
			AttributeType converterAttributeType = converter.getAttributeType();
	        useCalendar = (AttributeType.date.equals(converterAttributeType) || 
			        		AttributeType.dateTime.equals(converterAttributeType) ||
			        		AttributeType.timestamp.equals(converterAttributeType));
		}
		
        UIComponent result = null;
        if (useCalendar) {
            result = calendar(listBinding,
	            				text.getBinding(),
	                            title,
	                            required,
	                            false,
	                            text.getDisabledConditionName(),
	                            facesConverter);
        }
        else if (format != null) {
            result = maskField(listBinding,
								text.getBinding(),
								title,
								required,
								text.getDisabledConditionName(),
								length,
								format,
								facesConverter,
								text.getPixelWidth(),
								true);
        }
        else {
        	result = textField(listBinding,
								text.getBinding(),
								title,
								required,
								text.getDisabledConditionName(),
								length,
								facesConverter,
								text.getPixelWidth(),
								true);
        }
        
        return result;
	}

	@Override
	public UIComponent actionLink(String listBinding, Link link, String actionName) {
		// TODO do the tooltip and client validation, disabled, invisible thing,
		// Need the action, not just it's name
		return actionLink(link.getValue(),
							null,
							null,
							actionName,
							false,
							listBinding,
							link.getPixelWidth(),
							null,
							Boolean.FALSE,
							null,
							null,
							link.getInvisibleConditionName());
	}
	
	@Override
	public UIComponent report(org.skyve.metadata.view.Action action) {
		return reportButton(action.getDisplayName(), 
								action.getToolTip(), 
								action.getParameters(), 
								action.getDisabledConditionName(), 
								action.getInvisibleConditionName());
	}
	
	@Override
	public UIComponent action(String listBinding, 
								org.skyve.metadata.view.Action action, 
								ImplicitActionName name,
								String title) {
		return actionButton(title,
								action.getToolTip(),
								name,
								null,
								false,
								listBinding,
								null,
								null,
								action.getClientValidation(),
								action.getConfirmationText(),
								action.getDisabledConditionName(),
								action.getInvisibleConditionName());
	}
	
	private Panel panel(String title, String invisible, Integer pixelWidth) {
		Panel result = (Panel) a.createComponent(Panel.COMPONENT_TYPE);
		if (title != null) {
			result.setHeader(title);
		}

		setInvisible(result, invisible, null);
		setSize(result, null, pixelWidth, null, null, null, NINETY_EIGHT);
		setId(result);
		return result;
	}

	private HtmlOutputText text(String value, 
									String binding, 
									HorizontalAlignment textAlignment, 
									Integer pixelWidth,
									Integer pixelHeight, 
									String invisible, 
									boolean escape) {
		HtmlOutputText result = (HtmlOutputText) a.createComponent(HtmlOutputText.COMPONENT_TYPE);
		setId(result);
		if (value != null) {
			result.setValue(value);
		} else {
			result.setValueExpression("value", createValueExpressionFromBinding(binding, true, null, Object.class));
		}
		result.setEscape(escape);

		String style = null;
		if (HorizontalAlignment.left.equals(textAlignment)) {
			style = "text-align:left;";
		} 
		else if (HorizontalAlignment.centre.equals(textAlignment)) {
			style = "text-align:center;";
		} 
		else if (HorizontalAlignment.right.equals(textAlignment)) {
			style = "text-align:right;";
		}

		setSize(result, style, pixelWidth, null, pixelHeight, null, null);
		setInvisible(result, invisible, null);

		return result;
	}

	protected Password password(String bindingPrefix, 
									String binding, 
									String title, 
									boolean required, 
									String disabled,
									Integer pixelWidth, 
									boolean applyDefaultWidth) {
		Password result = (Password) input(Password.COMPONENT_TYPE, bindingPrefix, binding, title, required, disabled);
		setSize(result, null, pixelWidth, null, null, null, applyDefaultWidth ? ONE_HUNDRED : null);
		return result;
	}

	protected InputText textField(String bindingPrefix, 
									String binding, 
									String title, 
									boolean required, 
									String disabled,
									Integer maxLength, 
									Converter converter, 
									Integer pixelWidth, 
									boolean applyDefaultWidth) {
		InputText result = (InputText) input(InputText.COMPONENT_TYPE, 
												bindingPrefix, 
												binding, 
												title, 
												required,
												disabled);
		if (maxLength != null) {
			result.setMaxlength(maxLength.intValue());
		}
		if (converter != null) {
			result.setConverter(converter);
		}
		setSize(result, null, pixelWidth, null, null, null, applyDefaultWidth ? ONE_HUNDRED : null);
		return result;
	}

	private InputMask maskField(String bindingPrefix,
									String binding, 
									String title, 
									boolean required, 
									String disabled,
									Integer maxLength, 
									TextFormat format, 
									Converter converter, 
									Integer pixelWidth, 
									boolean applyDefaultWidth) {
		InputMask result = (InputMask) input(InputMask.COMPONENT_TYPE, 
												bindingPrefix, 
												binding, 
												title, 
												required,
												disabled);
		if (maxLength != null) {
			result.setMaxlength(maxLength.intValue());
		}
		result.setMask(determineMask(format));
		String existingStyle = null;
		TextCase textCase = format.getCase();
		if (textCase != null) {
			switch (textCase) {
			case upper:
				existingStyle = "text-transform:uppercase;";
				break;
			case capital:
				existingStyle = "text-transform:capitalize;";
				break;
			case lower:
				existingStyle = "text-transform:lowercase;";
				break;
			default:
				throw new IllegalStateException(textCase + " is not supported");
			}
		}
		if (converter != null) {
			result.setConverter(converter);
		}
		setSize(result, existingStyle, pixelWidth, null, null, null, applyDefaultWidth ? ONE_HUNDRED : null);
		return result;
	}

	/**
	 * My spec is A - alphanumeric # - digit L - letter
	 * 
	 * PF spec is 
	 * Character Description 
	 * 9 Digit (0 through 9) 
	 * a Letter (A through Z) 
	 * * Letter (A through Z) or number (0 through 9) 
	 * ? Allow optional matching of the rest of the expression
	 * 
	 * This method escapes anything that should be literal and then converts the
	 * expression taking into consideration the case setting.
	 * 
	 * @param text
	 * @return
	 */
	private static String determineMask(TextFormat format) {
		String result = null;

		if (format != null) {
			result = format.getMask();
			if (result != null) {
				// first escape characters with meaning
				result = result.replace("9", "\\9");
				result = result.replace("a", "\\a");
				result = result.replace("*", "\\*");
				result = result.replace("?", "\\?");

				// transpose my spec to the PF spec
				result = result.replace("A", "*");
				result = result.replace("#", "9");
				result = result.replace("L", "a");
			}
		}

		return result;
	}

	private Spinner spinner(String bindingPrefix, 
								String binding, 
								String title, 
								boolean required, 
								String disabled,
								Integer pixelWidth) {
		Spinner result = (Spinner) input(Spinner.COMPONENT_TYPE, bindingPrefix, binding, title, required, disabled);
		setSize(result, null, pixelWidth, null, null, null, null);
		return result;
	}

	private Calendar calendar(String bindingPrefix,
								String binding, 
								String title, 
								boolean required, 
								boolean mobile,
								String disabled, 
								Converter converter) {
		Calendar result = (Calendar) input(Calendar.COMPONENT_TYPE, bindingPrefix, binding, title, required, disabled);
		if (! mobile) {
			result.setMode("popup");
			result.setShowOn("button");
			result.setNavigator(true);
			result.setShowButtonPanel(true);
		}

		result.setYearRange("c-100:c+10");
		String converterName = converter.getClass().getSimpleName();
		if ("DD_MM_YYYY".equals(converterName)) {
			result.setPattern("dd/MM/yyyy");
			result.setMask("99/99/9999");
		} 
		else if ("DD_MMM_YYYY".equals(converterName)) {
			result.setPattern("dd-MMM-yyyy");
			result.setMask("99-aaa-9999");
		} 
		else if ("DD_MM_YYYY_HH_MI".equals(converterName)) {
			result.setPattern("dd/MM/yyyy hh:mm");
			result.setMask("99/99/9999 99:99");
		} 
		else if ("DD_MM_YYYY_HH24_MI".equals(converterName)) {
			result.setPattern("dd/MM/yyyy HH:mm");
			result.setMask("99/99/9999 99:99");
		} 
		else if ("DD_MM_YYYY_HH_MI".equals(converterName)) {
			result.setPattern("dd-MMM-yyyy hh:mm");
			result.setMask("99-aaa-9999 99:99");
		} 
		else if ("DD_MM_YYYY_HH24_MI".equals(converterName)) {
			result.setPattern("dd-MMM-yyyy HH:mm");
			result.setMask("99-aaa-9999 99:99");
		} 
		else if ("DD_MM_YYYY_HH_MI_SS".equals(converterName)) {
			result.setPattern("dd/MM/yyyy hh:mm:ss");
			result.setMask("99/99/9999 99:99:99");
		} 
		else if ("DD_MM_YYYY_HH24_MI_SS".equals(converterName)) {
			result.setPattern("dd/MM/yyyy HH:mm:ss");
			result.setMask("99/99/9999 99:99:99");
		} 
		else if ("DD_MM_YYYY_HH_MI_SS".equals(converterName)) {
			result.setPattern("dd-MMM-yyyy hh:mm:ss");
			result.setMask("99-aaa-9999 99:99:99");
		} 
		else if ("DD_MM_YYYY_HH24_MI_SS".equals(converterName)) {
			result.setPattern("dd-MMM-yyyy HH:mm:ss");
			result.setMask("99-aaa-9999 99:99:99");
		}
		result.setConverter(converter);
		return result;
	}

	protected InputTextarea textArea(String bindingPrefix, 
										String binding, 
										String title, 
										boolean required, 
										String disabled,
										Integer maxLength, 
										Integer pixelWidth, 
										Integer pixelHeight, 
										boolean applyDefaultWidth) {
		InputTextarea result = (InputTextarea) input(InputTextarea.COMPONENT_TYPE, 
														bindingPrefix, 
														binding, 
														title,
														required, 
														disabled);
		if (maxLength != null) {
			result.setMaxlength(maxLength.intValue());
		}
		setSize(result, null, pixelWidth, null, pixelHeight, null, applyDefaultWidth ? ONE_HUNDRED : null);
		return result;
	}

	private TabView tabView(String invisible) {
		TabView result = (TabView) a.createComponent(TabView.COMPONENT_TYPE);
		setInvisible(result, invisible, null);
		setId(result);
		// result.setDynamic(true);
		return result;
	}

	protected CommandButton actionButton(String title, 
											String tooltip, 
											ImplicitActionName implicitActionName,
											String actionName, 
											boolean inline, 
											String listBinding, 
											Integer pixelWidth, 
											Integer pixelHeight,
											Boolean clientValidation, 
											String confirmationText,
											String disabled, 
											String invisible) {
		CommandButton result = (CommandButton) a.createComponent(CommandButton.COMPONENT_TYPE);

		result.setValue(title);
		result.setTitle(tooltip);

		action(result, implicitActionName, actionName, listBinding, inline);
		setSize(result, null, pixelWidth, null, pixelHeight, null, null);
		setDisabled(result, disabled);
		setConfirmation(result, confirmationText);
		setId(result);

		// show/hide the implicit buttons - TODO base this also on security
		// privileges.
		if (ImplicitActionName.OK.equals(implicitActionName) || 
				ImplicitActionName.Save.equals(implicitActionName) || 
				ImplicitActionName.Cancel.equals(implicitActionName) || 
				ImplicitActionName.Delete.equals(implicitActionName)) {
			StringBuilder expression = new StringBuilder(128);
			expression.append("empty ").append(managedBeanName).append(".viewBinding");
			if (invisible == null) {
				result.setValueExpression("rendered",
											createValueExpressionFromBinding(null, 
																				expression.toString(), 
																				false, 
																				null, 
																				Boolean.class));
			} 
			else {
				setInvisible(result, invisible, expression.toString());
			}
		} 
		else if (ImplicitActionName.ZoomOut.equals(implicitActionName) || 
					ImplicitActionName.Remove.equals(implicitActionName)) {
			if (! inline) { // inline grids don't need invisible expression on remove button or link
				StringBuilder expression = new StringBuilder(128);
				expression.append("not empty ").append(managedBeanName).append(".viewBinding");
				if (invisible == null) {
					result.setValueExpression("rendered",
												createValueExpressionFromBinding(null, 
																					expression.toString(), 
																					false, 
																					null, 
																					Boolean.class));
				}
				else {
					setInvisible(result, invisible, expression.toString());
				}
			}
		}
		else {
			setInvisible(result, invisible, null);
		}

		if (ImplicitActionName.Cancel.equals(implicitActionName)) {
			result.setImmediate(true); // no validation
			result.setAjax(false); // normal request - which is slightly faster
		} 
		else if (ImplicitActionName.Remove.equals(implicitActionName) || 
					ImplicitActionName.Delete.equals(implicitActionName)) {
			result.setImmediate(true); // no validation
			result.setProcess(process); // process the current form
			result.setUpdate(update); // update all forms
			// Add the standard confirmation text if non exists
			if (confirmationText == null) {
				setConfirmation(result, "Do you want to delete this data?");
			}
		}
		else {
			result.setProcess(process); // process the current form
			result.setUpdate(update); // update all forms
		}

		return result;
	}

	/**
	 * Create a button with a href URL that looks like...
	 * http://localhost:8080/skyve/report/Bum.html?_f=html&_c=<webId>&_id=<id>&wee=poo&_n=Bum&_mod=<module>&_doc=<document>
	 */
	private Button reportButton(String value, 
									String title,
									List<Parameter> parameters,
									String disabled,
									String invisible) {
		StringBuilder href = new StringBuilder(128);
		String reportName = null;
		ReportFormat reportFormat = null;
		for (Parameter param : parameters) {
			String paramName = param.getName();
			String paramValue = param.getValue();
			String paramBinding = param.getBinding();
			if (AbstractWebContext.REPORT_NAME.equals(paramName)) {
				reportName = value;
			}
			else if (AbstractWebContext.REPORT_FORMAT.equals(paramName)) {
				reportFormat = ReportFormat.valueOf(value);
			}
			
			if (paramValue != null) {
				href.append(paramName).append('=').append(paramValue).append('&'); 
			}
			else if (paramBinding != null) {
				href.append(paramName).append("=#{").append(managedBeanName).append(".currentBean['{");
				href.append(paramBinding).append("}']}&"); 
			}
		}
		// remove last &
		int hrefLength = href.length();
		if (hrefLength > 0) {
			href.setLength(hrefLength - 1);
		}

		// if no report format parameter set, add it
		if (reportFormat == null) {
			reportFormat = ReportFormat.pdf;
			href.append('&').append(AbstractWebContext.REPORT_FORMAT).append('=').append(reportFormat);
		}

		// NB yes this is backwards coz its inserted
		href.insert(0, '?').insert(0, reportFormat).insert(0, '.').insert(0, reportName).insert(0, "report/");

		return linkButton(null, 
							null, 
							null,
							value,
							title,
							href.toString(),
							disabled,
							invisible,
							(ReportFormat.html.equals(reportFormat) ||
								ReportFormat.xhtml.equals(reportFormat)) ? 
									"_blank" : 
									null);
	}
	
	protected CommandLink actionLink(String title, 
										String tooltip, 
										ImplicitActionName implicitActionName,
										String actionName, 
										boolean inline, 
										String collectionName, 
										Integer pixelWidth, 
										Integer pixelHeight,
										Boolean clientValidation, 
										String confirmationText,
										String disabled, 
										String invisible) {
		CommandLink result = (CommandLink) a.createComponent(CommandLink.COMPONENT_TYPE);

		result.setValue(title);
		result.setTitle(tooltip);

		action(result, implicitActionName, actionName, collectionName, inline);

		setSize(result, null, pixelWidth, null, pixelHeight, null, null);
		setDisabled(result, disabled);
		setInvisible(result, invisible, null);
		setConfirmation(result, confirmationText);
		setId(result);

		if (ImplicitActionName.Cancel.equals(implicitActionName) || ImplicitActionName.OK.equals(implicitActionName)) {
			result.setAjax(false);
		} 
		else {
			result.setProcess(process);
			result.setUpdate(update);
		}

		return result;
	}

	private void action(UICommand command, 
							ImplicitActionName implicitActionName, 
							String actionName,
							String collectionName, 
							boolean inline) {
		command.setActionExpression(methodExpressionForAction(implicitActionName, actionName, collectionName, inline));
	}

	private Button linkButton(String icon, 
								String styleClass, 
								String style, 
								String value, 
								String title, 
								String href,
								String disabled, 
								String invisible, 
								String target) {
		Button result = button(icon, styleClass, style);
		result.setValue(value);
		result.setTitle(title);
		result.setValueExpression("href", ef.createValueExpression(elc, href, String.class));
		result.setTarget(target);

		setId(result);
		setDisabled(result, disabled);
		setInvisible(result, invisible, null);

		return result;
	}

	private UIOutput outputText(String listBinding, String binding) {
		ValueExpression ve = createValueExpressionFromBinding(listBinding, binding, true, null, String.class);
		UIOutput result = new UIOutput();
		result.setValueExpression("value", ve);
		setId(result);
		return result;
	}

	@Override
	public Spacer spacer(Integer pixelWidth, Integer pixelHeight) {
		Spacer result = (Spacer) a.createComponent(Spacer.COMPONENT_TYPE);
		setSize(result, null, pixelWidth, null, pixelHeight, null, null);
		setId(result);
		return result;
	}

	@Override
	public GraphicImage image(Integer pixelWidth, 
								Integer percentageWidth, 
								Integer pixelHeight,
								Integer percentageHeight, 
								String url, 
								String invisible,
								boolean border) {
		GraphicImage result = (GraphicImage) a.createComponent(GraphicImage.COMPONENT_TYPE);
		result.setUrl(url);
		setSize(result, border ? "border:1px solid gray;" : null, pixelWidth, percentageWidth, pixelHeight, percentageHeight, null);
		setInvisible(result, invisible, null);
		setId(result);
		return result;
	}

	private GraphicImage contentGraphicImage(Integer pixelWidth, 
												Integer percentageWidth, 
												Integer pixelHeight,
												Integer percentageHeight, 
												String binding, 
												String invisible) {
		GraphicImage result = (GraphicImage) a.createComponent(GraphicImage.COMPONENT_TYPE);

		StringBuilder expression = new StringBuilder(64);
		expression.append("#{").append(managedBeanName).append(".getResourceUrl('");
		expression.append(binding).append("')}");

		result.setValueExpression("value", ef.createValueExpression(elc, expression.toString(), String.class));
		setSize(result, "border:1px solid gray;", pixelWidth, percentageWidth, pixelHeight, percentageHeight, null);
		setInvisible(result, invisible, null);
		setId(result);
		return result;
	}

	private HtmlOutputLink contentLink(Integer pixelWidth, String text, String binding) {
		HtmlOutputLink result = (HtmlOutputLink) a.createComponent(HtmlOutputLink.COMPONENT_TYPE);

		StringBuilder expression = new StringBuilder(64);
		expression.append("#{").append(managedBeanName).append(".getResourceUrl('");
		expression.append(binding).append("')}");
		result.setValueExpression("value", ef.createValueExpression(elc, expression.toString(), String.class));

		if (text != null) {
			UIOutput outputText = (UIOutput) a.createComponent(UIOutput.COMPONENT_TYPE);
			outputText.setValue(text);
			result.getChildren().add(outputText);
		}

		result.setTarget("_blank");
		setSize(result, null, pixelWidth, null, null, null, null);
		setId(result);

		return result;
	}

	// TODO do the grids

	protected SelectBooleanCheckbox checkbox(String bindingPrefix, 
												String binding, 
												String title, 
												boolean required,
												String disabled) {
		return (SelectBooleanCheckbox) input(SelectBooleanCheckbox.COMPONENT_TYPE,
												bindingPrefix, 
												binding, 
												title, 
												required, 
												disabled);
	}

	protected ColorPicker colourPicker(String bindingPrefix, 
										String binding, 
										String title, 
										boolean required,
										Integer pixelWidth, 
										boolean applyDefaultWidth) {
		ColorPicker result = (ColorPicker) input(ColorPicker.COMPONENT_TYPE, 
													bindingPrefix, 
													binding, 
													title, 
													required,
													null);
		setSize(result, null, pixelWidth, null, null, null, applyDefaultWidth ? ONE_HUNDRED : null);
		return result;
	}

	private SelectOneMenu selectOneMenu(String bindingPrefix, 
											String binding, 
											String title, 
											boolean required,
											String disabled, 
											Integer pixelWidth) {
		SelectOneMenu result = (SelectOneMenu) input(SelectOneMenu.COMPONENT_TYPE, 
														bindingPrefix, 
														binding, 
														title,
														required, 
														disabled);
		// Do not default pixel width to 100% as it causes renderering issues on the drop button on the end.
		// The control sets its width by default based on the font metrics of the drop-down values.
		setSize(result, null, pixelWidth, null, null, null, null);
		result.setConverter(new SelectItemsBeanConverter());
		return result;
	}

	private SelectOneRadio selectOneRadio(String bindingPrefix, 
											String binding, 
											String title, 
											boolean required,
											String disabled) {
		SelectOneRadio result = (SelectOneRadio) input(SelectOneRadio.COMPONENT_TYPE, 
														bindingPrefix, 
														binding, 
														title,
														required, 
														disabled);
		result.setConverter(new SelectItemsBeanConverter());
		return result;
	}

	protected AutoComplete autoComplete(String bindingPrefix, 
											String binding, 
											String title, 
											boolean required,
											String disabled, 
											String displayBinding, 
											QueryDefinition query, 
											Integer pixelWidth,
											boolean dontDisplay) {
		AutoComplete result = (AutoComplete) input(AutoComplete.COMPONENT_TYPE, 
													bindingPrefix, 
													binding, 
													title, 
													required,
													disabled);
		result.setForceSelection(true);
		result.setDropdown(true);
		result.setVar(binding);
		StringBuilder expression = new StringBuilder(32);
		result.setValueExpression("itemLabel",
									createValueExpressionFromBinding(binding, displayBinding, true, null, String.class));
		result.setValueExpression("itemValue",
									createValueExpressionFromBinding(null, binding, false, null, BeanMapAdapter.class));
		result.setConverter(new AssociationAutoCompleteConverter());
		result.setScrollHeight(200);

		expression.setLength(0);
		expression.append("#{").append(managedBeanName).append(".complete}");
		result.setCompleteMethod(ef.createMethodExpression(elc, 
															expression.toString(), 
															List.class, 
															new Class[] {String.class}));

		Map<String, Object> attributes = result.getAttributes();
		attributes.put("module", query.getOwningModule().getName());
		attributes.put("query", query.getName());
		attributes.put("display", displayBinding);

		setSize(result, 
					dontDisplay ? "display:none" : null, 
					pixelWidth, 
					null, 
					null, 
					null,
					// width cannot be set correctly on this component when laid out in a table
					null); // applyDefaultWidth ? ONE_HUNDRED : null); 

		return result;
	}

	protected Button button(String icon, String styleClass, String style) {
		Button result = (Button) a.createComponent(Button.COMPONENT_TYPE);
		if (icon != null) {
			result.setIcon(icon);
		}
		if (styleClass != null) {
			result.setStyleClass(styleClass);
		}
		if (style != null) {
			result.setStyle(style);
		}
		setId(result);

		return result;
	}

	// this has a customisable toolbar for rich and html skyve editors.
	private Editor editor(String bindingPrefix, String binding, String title, boolean required, String disabled) {
		return (Editor) input(Editor.COMPONENT_TYPE, bindingPrefix, binding, title, required, disabled);
	}

	private DataTable dataTable(String binding, 
									String title, 
									String invisible, 
									boolean inline) {
		DataTable result = (DataTable) a.createComponent(DataTable.COMPONENT_TYPE);
		setId(result);
		setInvisible(result, invisible, null);
		addGridHeader(title, result);

		result.setVar(binding);
		result.setValueExpression("value", createValueExpressionFromBinding(binding, true, null, List.class));

		if (! inline) {
			String id = result.getId();
			result.setWidgetVar(id);
			result.setSelectionMode("single");
			result.setValueExpression("rowKey",
										createValueExpressionFromBinding(result.getVar(),
																			Bean.DOCUMENT_ID, 
																			true, 
																			null, 
																			String.class));

			AjaxBehavior ajax = (AjaxBehavior) a.createBehavior(AjaxBehavior.BEHAVIOR_ID);
			StringBuilder expression = new StringBuilder(64);
			expression.append("#{").append(managedBeanName).append('.');
			expression.append(ImplicitActionName.Navigate.name().toLowerCase()).append('}');
			MethodExpression me = ef.createMethodExpression(elc, expression.toString(), null, new Class[0]);
			ajax.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(me, me));
			result.addClientBehavior("rowSelect", ajax);
		}

		return result;
	}

	protected DataList dataList(String binding, String title, String invisible) {
		DataList result = (DataList) a.createComponent(DataList.COMPONENT_TYPE);
		setId(result);
		setInvisible(result, invisible, null);
		addGridHeader(title, result);

		result.setVar(binding);
		result.setValueExpression("value", createValueExpressionFromBinding(binding, true, null, List.class));

		return result;
	}

	private void addGridHeader(String title, 
								UIComponent dataTableOrList) {
		if (title != null) {
			UIOutput text = (UIOutput) a.createComponent(UIOutput.COMPONENT_TYPE);
			text.setValue(title);
			setId(text);
			dataTableOrList.getFacets().put("header", text);
		}
/*
		if (singularDocumentAlias != null) {
			UIComponent buttonOrLink = buttons ? 
										actionButton("Add", 
														"Add a new " + singularDocumentAlias, 
														ImplicitActionName.Add, 
														null, 
														inline,
														binding, 
														null, 
														null, 
														Boolean.TRUE, 
														null,
														null, 
														null) :
										actionLink("Add a new " + singularDocumentAlias, 
													"New Record", 
													ImplicitActionName.Add, 
													null,
													inline, 
													binding, 
													null, 
													null, 
													Boolean.TRUE, 
													null,
													null, 
													null);
			dataTableOrList.getFacets().put("footer", buttonOrLink);
		}
*/
	}

	protected AccordionPanel accordionPanel(String invisible) {
		AccordionPanel result = (AccordionPanel) a.createComponent(AccordionPanel.COMPONENT_TYPE);
		setId(result);
		setInvisible(result, invisible, null);
		return result;
	}

	private Column column(String listBinding, 
							String sortBinding, 
							String title, 
							HorizontalAlignment alignment,
							boolean noWrap, 
							Integer pixelWidth) {
		Column result = (Column) a.createComponent(Column.COMPONENT_TYPE);
		setId(result);

		result.setHeaderText(title);
		if (sortBinding != null) {
			result.setValueExpression("sortBy",
										createValueExpressionFromBinding(listBinding, sortBinding, true, null, Object.class));
		}

		StringBuilder style = new StringBuilder(64);
		if (pixelWidth != null) {
			style.append("width:").append(pixelWidth).append("px;");
		}
		if (noWrap) {
			style.append("white-space:nowrap;");
		}
		if ((alignment != null) && (!HorizontalAlignment.left.equals(alignment))) {
			style.append("text-align:").append(HorizontalAlignment.centre.equals(alignment) ? "center" : "right").append(" !important;");
		}
		if (style.length() > 0) {
			result.setStyle(style.toString());
		}

		return result;
	}

	private UISelectItems selectItems(String listBinding, String binding, boolean includeEmptyItems) {
		UISelectItems result = (UISelectItems) a.createComponent(UISelectItems.COMPONENT_TYPE);
		setId(result);
		StringBuilder expression = new StringBuilder(32);
		expression.append("getSelectItems('").append(binding).append("',").append(includeEmptyItems).append(')');
		ValueExpression valueExpression = null;
		if (listBinding != null) {
			valueExpression = createValueExpressionFromBinding(listBinding, expression.toString(), false, null, List.class);
		}
		else {
			valueExpression = createValueExpressionFromBinding(expression.toString(), false, null, List.class);
		}
		result.setValueExpression("value", valueExpression);

		return result;
	}

	private UIInput input(String componentType, 
							String listBinding, 
							String binding,
							String title, 
							boolean required,
							String disabled) {
		UIInput result = (UIInput) a.createComponent(componentType);
		setId(result);
		if (listBinding != null) {
			result.setValueExpression("value",
										createValueExpressionFromBinding(listBinding, binding, true, null, Object.class));
		}
		else {
			result.setValueExpression("value", createValueExpressionFromBinding(binding, true, null, Object.class));
		}
		result.setValueExpression("title",
									ef.createValueExpression(elc, required ? title + " *" : title, String.class));

		// Cannot utilise the faces required attributes as some requests need to ignore required-ness.
		// eg - triggered actions on widget events.
		// Setting required attribute to an expression worked server-side but the client-side message integration didn't.
		// result.setValueExpression("required", ef.createValueExpression(required ? "true" : "false", Boolean.class));
		// So we use the requiredMessage to perform the check ourselves based on clientValidation attribute
		if (required) {
			result.setRequiredMessage(title + " is required");
		}
		setDisabled(result, disabled);
		return result;
	}

	private void setConfirmation(UIComponentBase component, String confirmationText) {
		if (confirmationText != null) {
			ConfirmBehavior confirm = (ConfirmBehavior) a.createBehavior(ConfirmBehavior.BEHAVIOR_ID);
			confirm.setMessage(confirmationText);
			component.addClientBehavior("click", confirm);
		}
	}
	
/*
	private HtmlForm form() {
		HtmlForm result = (HtmlForm) a.createComponent(HtmlForm.COMPONENT_TYPE);
		setId(result);

		return result;
	}

	private Fieldset fieldset(String legend, String invisible) {
		Fieldset result = (Fieldset) a.createComponent(Fieldset.COMPONENT_TYPE);
		if (legend != null) {
			result.setLegend(legend);
		}
		setInvisible(result, invisible, null);
		setId(result);
		return result;
	}
	
	private UIParameter parameter(String name, Object value) {
		UIParameter result = (UIParameter) a.createComponent(UIParameter.COMPONENT_TYPE);
		result.setName(name);
		result.setValue(value);
		setId(result);
		return result;
	}
	
	private ProgressBar progressBar() {
		ProgressBar result = (ProgressBar) a.createComponent(ProgressBar.COMPONENT_TYPE);
		setId(result);
		return result;
	}
	
	private TriStateCheckbox triStateCheckbox(String bindingPrefix, 
												String binding, 
												String title, 
												boolean required,
												String disabled) {
		return (TriStateCheckbox) input(TriStateCheckbox.COMPONENT_TYPE, 
											bindingPrefix, 
											binding, 
											title, 
											required,
											disabled);
	}

	private SelectManyCheckbox manyCheckbox(String bindingPrefix, 
												String binding, 
												String title, 
												boolean required,
												String disabled) {
		return (SelectManyCheckbox) input(SelectManyCheckbox.COMPONENT_TYPE, 
											bindingPrefix, 
											binding, 
											title, 
											required,
											disabled);
	}
	
	private FileUpload fileUpload(String bindingPrefix, 
									String binding, 
									String title, 
									boolean required,
									String disabled) {
		return (FileUpload) input(FileUpload.COMPONENT_TYPE, bindingPrefix, binding, title, required, disabled);
	}
*/
	/**
	 * <h:link outcome="reviewBatch" value="Restart" rendered=#{batch.renderRestart}">
	 *     <f:param name="c" value=#{batch.row.batchHeader.identifier.clientId}" />
	 *     <f:param name="b" value="#{batch.row.batchHeader.identifier.batchNumber}" />
	 * </h:link>
	 */
/*
	private HtmlOutputLink outputLink(String value, String outcome, String disabled, String invisible) {
		HtmlOutputLink result = (HtmlOutputLink) a.createComponent(HtmlOutputLink.COMPONENT_TYPE);
		result.setValue(value);
		setDisabled(result, disabled);
		setInvisible(result, invisible, null);
		setId(result);
		return result;
	}
*/	
}
