package io.thoqbk.tholangforfun.ast.statements;

import java.util.ArrayList;
import java.util.List;

import io.thoqbk.tholangforfun.Token;

public class Block extends Statement {
    private List<Statement> statements = new ArrayList<>();

    public Block(Token token) {
        super(token);
    }

    public List<Statement> getStatements() {
        return statements;
    }

}
