package nl.vu.kai.companion.util;

import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxObjectRenderer;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxPrefixNameShortFormProvider;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.ShortFormProvider;

import java.io.StringWriter;

public class OWLFormatter {

    private final ManchesterOWLSyntaxObjectRenderer renderer;

    private final StringBuffer stringBuffer;

    public OWLFormatter(OWLOntology referenceOntology) {
        StringWriter stringWriter = new StringWriter();
        stringBuffer = stringWriter.getBuffer();
        ShortFormProvider shortFormProvider =
                new ShortFormProvider() {
                    @Override
                    public String getShortForm(OWLEntity owlEntity) {
                        return owlEntity.getIRI().getShortForm();
                    }
                };
                //new ManchesterOWLSyntaxPrefixNameShortFormProvider(referenceOntology)
        renderer = new ManchesterOWLSyntaxObjectRenderer(stringWriter, shortFormProvider);
    }

    public String format(OWLAxiom axiom) {
        stringBuffer.delete(0, stringBuffer.length());
        axiom.accept(renderer);
        //stringWriter.flush();
        return stringBuffer.toString();
    }
}
