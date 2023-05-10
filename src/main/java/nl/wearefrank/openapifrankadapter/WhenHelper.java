package nl.wearefrank.openapifrankadapter;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import java.io.IOException;

public class WhenHelper implements Helper<Object> {

    private static final String OPERATOR_EQ = "eq";
    private static final String OPERATOR_NOTEQ = "noteq";
    private static final String OPERATOR_GT = "gt";
    private static final String OPERATOR_OR = "or";
    private static final String OPERATOR_AND = "and";
    private static final String OPERATOR_MODULO = "%";

    @Override
    public CharSequence apply(Object operand1, Options options) throws IOException {
        Object operator = options.param(0);
        Object operand2 = options.param(1);

        boolean result = false;

        switch (operator.toString()) {
            case OPERATOR_EQ:
                result = operand1.equals(operand2);
                break;
            case OPERATOR_NOTEQ:
                result = !operand1.equals(operand2);
                break;
            case OPERATOR_GT:
                result = Double.parseDouble(operand1.toString()) > Double.parseDouble(operand2.toString());
                break;
            case OPERATOR_OR:
                result = (boolean) operand1 || (boolean) operand2;
                break;
            case OPERATOR_AND:
                result = (boolean) operand1 && (boolean) operand2;
                break;
            case OPERATOR_MODULO:
                result = Double.parseDouble(operand1.toString()) % Double.parseDouble(operand2.toString()) == 0;
                break;
        }

        if (result) {
            return options.fn();
        } else {
            return "";
        }
    }
}
