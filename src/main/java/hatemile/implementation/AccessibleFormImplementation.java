/*
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package hatemile.implementation;

import hatemile.AccessibleForm;
import hatemile.util.Configure;
import hatemile.util.HTMLDOMElement;
import hatemile.util.HTMLDOMParser;

import java.util.Collection;

/**
 * The AccessibleFormImplementation class is official implementation of
 * AccessibleForm interface.
 */
public class AccessibleFormImplementation implements AccessibleForm {
	
	/**
	 * The HTML parser.
	 */
	protected final HTMLDOMParser parser;
	
	/**
	 * The prefix of generated ids.
	 */
	protected final String prefixId;
	
	/**
	 * The name of attribute for not modify the elements.
	 */
	protected final String dataIgnore;
	
	/**
	 * Initializes a new object that manipulate the accessibility of the forms
	 * of parser.
	 * @param parser The HTML parser.
	 * @param configure The configuration of HaTeMiLe.
	 */
	public AccessibleFormImplementation(HTMLDOMParser parser, Configure configure) {
		this.parser = parser;
		dataIgnore = "data-ignoreaccessibilityfix";
		prefixId = configure.getParameter("prefix-generated-ids");
	}
	
	/**
	 * Returns the appropriate value for attribute aria-autocomplete of field.
	 * @param field The field.
	 * @return The ARIA value of field.
	 */
	protected String getARIAAutoComplete(HTMLDOMElement field) {
		String tagName = field.getTagName();
		String type = null;
		if (field.hasAttribute("type")) {
			type = field.getAttribute("type").toLowerCase();
		}
		if ((tagName.equals("TEXTAREA")) || ((tagName.equals("INPUT"))
				&& (!(("button".equals(type)) || ("submit".equals(type))
					|| ("reset".equals(type)) || ("image".equals(type))
					|| ("file".equals(type)) || ("checkbox".equals(type))
					|| ("radio".equals(type)) || ("hidden".equals(type)))))) {
			String value = null;
			if (field.hasAttribute("autocomplete")) {
				value = field.getAttribute("autocomplete").toLowerCase();
			} else {
				HTMLDOMElement form = parser.find(field).findAncestors("form").firstResult();
				if ((form == null) && (field.hasAttribute("form"))) {
					form = parser.find("#" + field.getAttribute("form")).firstResult();
				}
				if ((form != null) && (form.hasAttribute("autocomplete"))) {
					value = form.getAttribute("autocomplete").toLowerCase();
				}
			}
			if ("on".equals(value)) {
				return "both";
			} else if ((field.hasAttribute("list")) && (parser
					.find("datalist[id=\"" + field.getAttribute("list") + "\"]").firstResult() != null)) {
				return "list";
			} else if ("off".equals(value)) {
				return "none";
			}
		}
		return null;
	}
	
	/**
	 * Returns the labels of field.
	 * @param field The field.
	 * @return The labels of field.
	 */
	protected Collection<HTMLDOMElement> getLabels(HTMLDOMElement field) {
		Collection<HTMLDOMElement> labels = null;
		if (field.hasAttribute("id")) {
			labels = parser.find("label[for=\"" + field.getAttribute("id") + "\"]").listResults();
		}
		if ((labels == null) || (labels.isEmpty())) {
			labels = parser.find(field).findAncestors("label").listResults();
		}
		return labels;
	}
	
	public void fixRequiredField(HTMLDOMElement requiredField) {
		if (requiredField.hasAttribute("required")) {
			requiredField.setAttribute("aria-required", "true");
		}
	}
	
	public void fixRequiredFields() {
		Collection<HTMLDOMElement> requiredFields = parser.find("[required]").listResults();
		for (HTMLDOMElement requiredField : requiredFields) {
			if (!requiredField.hasAttribute(dataIgnore)) {
				fixRequiredField(requiredField);
			}
		}
	}
	
	public void fixRangeField(HTMLDOMElement rangeField) {
		if (rangeField.hasAttribute("min")) {
			rangeField.setAttribute("aria-valuemin", rangeField.getAttribute("min"));
		}
		if (rangeField.hasAttribute("max")) {
			rangeField.setAttribute("aria-valuemax", rangeField.getAttribute("max"));
		}
	}
	
	public void fixRangeFields() {
		Collection<HTMLDOMElement> rangeFields = parser.find("[min],[max]").listResults();
		for (HTMLDOMElement rangeField : rangeFields) {
			if (!rangeField.hasAttribute(dataIgnore)) {
				fixRangeField(rangeField);
			}
		}
	}

	public void fixAutoCompleteField(HTMLDOMElement autoCompleteField) {
		String ariaAutoComplete = getARIAAutoComplete(autoCompleteField);
		if (ariaAutoComplete != null) {
			autoCompleteField.setAttribute("aria-autocomplete", ariaAutoComplete);
		}
	}

	public void fixAutoCompleteFields() {
		Collection<HTMLDOMElement> elements = parser
				.find("input[autocomplete],textarea[autocomplete],form[autocomplete] input,form[autocomplete] textarea,[list],[form]").listResults();
		for (HTMLDOMElement element : elements) {
			if (!element.hasAttribute(dataIgnore)) {
				fixAutoCompleteField(element);
			}
		}
	}
}