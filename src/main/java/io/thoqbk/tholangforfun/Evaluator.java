package io.thoqbk.tholangforfun;

import java.util.List;

import io.thoqbk.tholangforfun.ast.Program;
import io.thoqbk.tholangforfun.ast.expressions.Bool;
import io.thoqbk.tholangforfun.ast.expressions.Expression;
import io.thoqbk.tholangforfun.ast.expressions.Infix;
import io.thoqbk.tholangforfun.ast.expressions.Int;
import io.thoqbk.tholangforfun.ast.expressions.Prefix;
import io.thoqbk.tholangforfun.ast.statements.Block;
import io.thoqbk.tholangforfun.ast.statements.ExpressionStm;
import io.thoqbk.tholangforfun.ast.statements.If;
import io.thoqbk.tholangforfun.ast.statements.Return;
import io.thoqbk.tholangforfun.ast.statements.Statement;
import io.thoqbk.tholangforfun.eval.BoolResult;
import io.thoqbk.tholangforfun.eval.EvalResult;
import io.thoqbk.tholangforfun.eval.IntResult;
import io.thoqbk.tholangforfun.eval.NullResult;
import io.thoqbk.tholangforfun.exceptions.EvalException;

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
        } else if (statement.is(If.class)) {
            return eval(statement.as(If.class));
        } else if (statement.is(Block.class)) {
            return eval(statement.as(Block.class));
        }
        throw new EvalException("Unknown statement " + statement);
    }

    private EvalResult eval(ExpressionStm expressionStm) {
        return eval(expressionStm.getExpression());
    }

    private EvalResult eval(If ifStm) {
        return isTruthy(eval(ifStm.getCondition())) ? eval(ifStm.getIfBody()) : eval(ifStm.getElseBody());
    }

    private EvalResult eval(Block block) {
        EvalResult retVal = new NullResult();
        if (block == null) {
            return retVal;
        }
        for (Statement stm : block.getStatements()) {
            EvalResult result = eval(stm);
            if (stm.is(ExpressionStm.class)) {
                retVal = result;
            } else if (stm.is(Return.class)) {
                retVal = result;
                break;
            }
        }
        return retVal;
    }

    private EvalResult eval(Expression expression) {
        if (expression.is(Int.class)) {
            return new IntResult(expression.as(Int.class).getValue());
        } else if (expression.is(Bool.class)) {
            return new BoolResult(expression.as(Bool.class).getValue());
        } else if (expression.is(Prefix.class)) {
            return eval(expression.as(Prefix.class));
        } else if (expression.is(Infix.class)) {
            return eval(expression.as(Infix.class));
        }
        throw new EvalException("Unknown expression " + expression);
    }

    private EvalResult eval(Prefix prefix) {
        EvalResult base = eval(prefix.getRight());
        switch (prefix.getToken().getType()) {
            case MINUS: {
                return new IntResult(-base.as(IntResult.class).getValue());
            }
            case BANG: {
                boolean truthyIntCase = base != null && base.is(IntResult.class)
                        && base.as(IntResult.class).getValue() == 0;
                boolean truthyBoolCase = base != null && base.is(BoolResult.class)
                        && !base.as(BoolResult.class).getValue();
                return new BoolResult(base == null || truthyIntCase || truthyBoolCase);
            }
            default: {
                throw new EvalException("Invalid prefix operator '" + prefix.getToken().getType() + "'");
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
            case GT: {
                return new BoolResult(left.as(IntResult.class).getValue() > right.as(IntResult.class).getValue());
            }
            case LT: {
                return new BoolResult(left.as(IntResult.class).getValue() < right.as(IntResult.class).getValue());
            }
            case EQ: {
                return evalEqual(left, right);
            }
            case NOT_EQ: {
                return new BoolResult(!evalEqual(left, right).as(BoolResult.class).getValue());
            }
            default: {
                throw new EvalException("Invalid infix operator '" + infix.getToken().getType() + "'");
            }
        }
    }

    private EvalResult evalEqual(EvalResult left, EvalResult right) {
        boolean intCase = left.is(IntResult.class) && right.is(IntResult.class)
                && left.as(IntResult.class).getValue() == right.as(IntResult.class).getValue();
        boolean boolCase = left.is(BoolResult.class) && right.is(BoolResult.class)
                && left.as(BoolResult.class).getValue() == right.as(BoolResult.class).getValue();
        return new BoolResult(intCase || boolCase);
    }

    private boolean isTruthy(EvalResult result) {
        boolean boolCase = result != null && result.is(BoolResult.class) && result.as(BoolResult.class).getValue();
        boolean intCase = result != null && result.is(IntResult.class) && result.as(IntResult.class).getValue() != 0;
        return boolCase || intCase;
    }
}
