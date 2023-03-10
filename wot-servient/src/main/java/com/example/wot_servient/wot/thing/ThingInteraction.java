package com.example.wot_servient.wot.thing;

import com.example.wot_servient.wot.thing.form.Form;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract representation of a Thing Interaction (inherited from {@link
 * com.example.wot_servient.wot.thing.action.ThingAction}, {@link com.example.wot_servient.wot.thing.event.ThingEvent} and {@link
 * com.example.wot_servient.wot.thing.property.ThingProperty})
 *
 * @param <T>
 */
public abstract class ThingInteraction<T> {

	@JsonInclude(JsonInclude.Include.NON_NULL) protected String name;
	@JsonInclude(JsonInclude.Include.NON_NULL) protected String description;
	@JsonInclude(JsonInclude.Include.NON_NULL) protected Map<String, String> descriptions;
	@JsonInclude(JsonInclude.Include.NON_EMPTY) protected List<Form> forms = new ArrayList<>();
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	protected Map<String, Map<String, Object>> uriVariables = new HashMap<>();

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Map<String, String> getDescriptions() {
		return descriptions;
	}

	public List<Form> getForms() {
		return forms;
	}

	public Map<String, Map<String, Object>> getUriVariables() {
		return uriVariables;
	}

	public T setForms(List<Form> forms) {
		this.forms = forms;
		return (T) this;
	}

	public T addForm(Form form) {
		forms.add(form);
		return (T) this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, description, descriptions, forms, uriVariables);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ThingInteraction)) {
			return false;
		}
		ThingInteraction<?> that = (ThingInteraction<?>) o;
		return Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(descriptions, that.descriptions) && Objects.equals(forms, that.forms) && Objects.equals(uriVariables, that.uriVariables);
	}
}
