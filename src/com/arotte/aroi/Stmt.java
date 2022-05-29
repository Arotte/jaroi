package com.arotte.aroi;

import java.util.List;

/**Stmt.java
 *
 * THIS IS A GENERATED FILE.
 * DO NOT MODIFY!
 *
 * Generated by com.arotte.tool.GenerateAst.java
 */
abstract class Stmt {
  interface Visitor<R> {
    R visitBlockStmt(Block stmt);
    R visitExpressionStmt(Expression stmt);
    R visitIfStmt(If stmt);
    R visitPrintStmt(Print stmt);
    R visitVarStmt(Var stmt);
  }

  static class Block extends Stmt {
    // fields
    final List<Stmt> statements;
    // constructor
    Block(List<Stmt> statements) {
      this.statements = statements;
    }
    // visitor pattern
    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }
  }

  static class Expression extends Stmt {
    // fields
    final Expr expression;
    // constructor
    Expression(Expr expression) {
      this.expression = expression;
    }
    // visitor pattern
    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }
  }

  static class If extends Stmt {
    // fields
    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;
    // constructor
    If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }
    // visitor pattern
    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }
  }

  static class Print extends Stmt {
    // fields
    final Expr expression;
    // constructor
    Print(Expr expression) {
      this.expression = expression;
    }
    // visitor pattern
    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }
  }

  static class Var extends Stmt {
    // fields
    final Token name;
    final Expr initializer;
    // constructor
    Var(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }
    // visitor pattern
    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }
  }

  abstract <R> R accept(Visitor<R> visitor);
}
