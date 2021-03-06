package ma.glasnost.orika.impl.generator.specification;

import static java.lang.String.format;
import static ma.glasnost.orika.impl.generator.SourceCodeContext.statement;
import ma.glasnost.orika.impl.generator.SourceCodeContext;
import ma.glasnost.orika.impl.generator.VariableRef;
import ma.glasnost.orika.metadata.FieldMap;

public class Convert extends AbstractSpecification {

    public boolean appliesTo(FieldMap fieldMap) {
        return fieldMap.getConverterId() != null || mapperFactory.getConverterFactory().canConvert(fieldMap.getAType(), fieldMap.getBType());
    }

    public String generateEqualityTestCode(FieldMap fieldMap, VariableRef source, VariableRef destination, SourceCodeContext code) {
        if (source.type().isPrimitive() || destination.type().isPrimitive()) {
            return format("(%s == %s.convert(%s, %s))", destination.asWrapper(), code.usedConverter(source.getConverter()), source.asWrapper(), code.usedType(destination));
        } else {
            return format("(%s != null && %s.equals(%s.convert(%s, %s)))", source, destination, code.usedConverter(source.getConverter()), source.asWrapper(), code.usedType(destination));
        }
        
    }

    public String generateMappingCode(FieldMap fieldMap, VariableRef source, VariableRef destination, SourceCodeContext code) {
        
        String statement = destination.assign("%s.convert(%s, %s)", code.usedConverter(source.getConverter()), source.asWrapper(), code.usedType(destination));
        
        boolean shouldSetNull = shouldMapNulls(fieldMap, code) && !destination.isPrimitive();
        
        if (source.isPrimitive()) {
            return statement(statement);
        } else {
        	String elseSetNull   = shouldSetNull ? (" else { \n" + destination.assignIfPossible("null")) + ";\n }" : "";
            return statement(source.ifNotNull() + "{ \n" + statement) + "\n}" + elseSetNull;
        }
    }
    
}
