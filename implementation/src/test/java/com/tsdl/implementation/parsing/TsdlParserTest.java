package com.tsdl.implementation.parsing;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;
import org.tsdl.grammar.TsdlLexer;
import org.tsdl.grammar.TsdlParser;

import static org.assertj.core.api.Assertions.assertThat;

public class TsdlParserTest {
    @Test
    void parseProperties() {
        String properties = """
          # This is my test.properties file
          favoriteFood=kun pao chicken
          # another comment
          favoriteVegetable=artichoke
          favoriteSoda=Dr Pepper
          """;

        var lexer = new TsdlLexer(CharStreams.fromString(properties));
        var tokens = new CommonTokenStream(lexer);
        var parser = new TsdlParser(tokens);
        var walker = new ParseTreeWalker();
        var tsdlWalker = new PropertyFileListener();
        walker.walk(tsdlWalker, parser.propertiesFile());

        var result = tsdlWalker.getPropertyFile();
        assertThat(result.getProperties()).hasSize(3);
        assertThat(result.get("favoriteFood")).isEqualTo("kun pao chicken");
        assertThat(result.get("favoriteVegetable")).isEqualTo("artichoke");
        assertThat(result.get("favoriteSoda")).isEqualTo("Dr Pepper");
    }
}
