package no.kantega.publishing.modules.forms.filter;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.publishing.modules.forms.validate.FormError;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class FormFillFilterTest  {

    @Test
    public void testInputText() throws SystemException {
        FilterPipeline pipeline = new FilterPipeline();

        Map<String, String[]> params = new HashMap<>();
        params.put("name", new String[] {"Lorum ipsum"});

        FormFillFilter filter = new FormFillFilter(params, new ArrayList<FormError>());

        pipeline.addFilter(filter);

        String input = "<input type=\"text\" name=\"name\"><input type=\"text\" name=\"email\">";
        String output ="<input type=\"text\" name=\"name\" value=\"Lorum ipsum\"><input type=\"text\" name=\"email\">";
        assertEquals(output, pipeline.filter(input));
    }

    @Test
    public void testInputTextArea() throws SystemException {
        FilterPipeline pipeline = new FilterPipeline();

        Map<String, String[]> params = new HashMap<>();
        params.put("name", new String[] {"Lorum ipsum"});

        FormFillFilter filter = new FormFillFilter(params, new ArrayList<FormError>());

        pipeline.addFilter(filter);

        String input = "<input type=\"text\" name=\"x\" value=\"\"><textarea name=\"name\"></textarea>";
        String output ="<input type=\"text\" name=\"x\" value=\"\"><textarea name=\"name\">Lorum ipsum</textarea>";
        assertEquals(output, pipeline.filter(input));
    }

    @Test
    public void testInputRadio() throws SystemException {
        FilterPipeline pipeline = new FilterPipeline();

        Map<String, String[]> params = new HashMap<>();
        params.put("choice", new String[] {"one"});

        FormFillFilter filter = new FormFillFilter(params, new ArrayList<FormError>());

        pipeline.addFilter(filter);

        String input = "<input type=\"radio\" name=\"choice\" value=\"one\"><input type=\"radio\" name=\"choice\" value=\"two\" checked>";
        String output = "<input type=\"radio\" name=\"choice\" value=\"one\" checked><input type=\"radio\" name=\"choice\" value=\"two\">";
        assertEquals(output, pipeline.filter(input));

    }
    @Test
    public void testInputSelect() throws SystemException {
        FilterPipeline pipeline = new FilterPipeline();

        Map<String, String[]> params = new HashMap<>();
        params.put("choice", new String[] {"one"});

        FormFillFilter filter = new FormFillFilter(params, new ArrayList<FormError>());

        pipeline.addFilter(filter);

        String input = "<select name=\"choice\"><option value=\"one\">One</option><option value=\"two\">Two</option></select>";
        String output = "<select name=\"choice\"><option value=\"one\" selected>One</option><option value=\"two\">Two</option></select>";
        assertEquals(output, pipeline.filter(input));
    }

}