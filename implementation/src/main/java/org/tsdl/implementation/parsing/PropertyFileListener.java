package org.tsdl.implementation.parsing;


import org.tsdl.implementation.model.PropertyFile;
import org.tsdl.grammar.TsdlBaseListener;
import org.tsdl.grammar.TsdlParser;

public class PropertyFileListener extends TsdlBaseListener {
    private final PropertyFile propertyFile = new PropertyFile();

    private String currentKey;

    private String currentValue;

    @Override
    public void enterRow(TsdlParser.RowContext ctx) {
        currentKey = null;
        currentValue = null;
    }

    @Override
    public void exitRow(TsdlParser.RowContext ctx) {
        if (ctx.comment() != null || ctx.decl() == null) {
            return;
        }

        if (currentKey == null || currentValue == null) {
            throw new IllegalStateException("neither currentKey nor currentValue should be null at this point");
        }

        propertyFile.add(currentKey,currentValue);
    }

    @Override
    public void enterKey(TsdlParser.KeyContext ctx) {
        currentKey = ctx.getText();
    }

    @Override
    public void enterValue(TsdlParser.ValueContext ctx) {
        currentValue = ctx.getText();
    }

    public PropertyFile getPropertyFile() {
        return propertyFile;
    }
}
