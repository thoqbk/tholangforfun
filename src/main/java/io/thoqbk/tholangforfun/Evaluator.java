package io.thoqbk.tholangforfun;

import java.util.List;

import io.thoqbk.tholangforfun.ast.Program;
import io.thoqbk.tholangforfun.ast.expressions.Expression;
import io.thoqbk.tholangforfun.ast.expressions.Infix;
import io.thoqbk.tholangforfun.ast.expressions.Int;
import io.thoqbk.tholangforfun.ast.expressions.Prefix;
import io.thoqbk.tholangforfun.ast.statements.ExpressionStm;
import io.thoqbk.tholangforfun.ast.statements.Statement;
import io.thoqbk.tholangforfun.eval.EvalResult;
import io.thoqbk.tholangforfun.eval.IntResult;

public class Evaluator {
    public EvalResult eval(Program program) {
        List<Statement> statements = program.getStatements();
        EvalResult retVal = null;
        for (Statement statement : statements) {
            retVal = eval(statement);
        }
        return retVal;
    }

    private EvalResult eval(Statement statement) {
        if (statement.is(ExpressionStm.class)) {
            return eval(statement.as(ExpressionStm.class));
        }
        return null;
    }

    private EvalResult eval(ExpressionStm expressionStm) {
        return eval(expressionStm.getExpression());
    }

    private EvalResult eval(Expression expression) {
        if (expression.is(Int.class)) {
            return new IntResult(expression.as(Int.class).getValue());
        } else if (expression.is(Prefix.class)) {
            return eval(expression.as(Prefix.class));
        } else if (expression.is(Infix.class)) {
            return eval(expression.as(Infix.class));
        }
        return null;
    }

    private EvalResult eval(Prefix prefix) {
        EvalResult base = eval(prefix.getRight());
        switch (prefix.getToken().getType()) {
            case MINUS: {
                return new IntResult(-base.as(IntResult.class).getValue());
            }
            default: {
                return null;
            }
        }
    }

    private EvalResult eval(Infix infix) {
        EvalResult left = eval(infix.getLeft());
        EvalResult right = eval(infix.getRight());
        switch (infix.getToken().getType()) {
            case PLUS: {
                return new IntResult(
                        left.as(IntResult.class).getValue() + right.as(IntResult.class).getValue());
            }
            case MINUS: {
                return new IntResult(
                        left.as(IntResult.class).getValue() - right.as(IntResult.class).getValue());
            }
            case ASTERISK: {
                return new IntResult(
                        left.as(IntResult.class).getValue() * right.as(IntResult.class).getValue());
            }
            case SLASH: {
                return new IntResult(
                        left.as(IntResult.class).getValue() / right.as(IntResult.class).getValue());
            }
            default: {
                return null;
            }
        }
    }
}
