/*
 * Copyright 2010 Pablo Arrighi, Alex Concha, Miguel Lezama for version 1.
 * Copyright 2013 Pablo Arrighi, Miguel Lezama, Kevin Mazet for version 2.    
 *
 * This file is part of GOOL.
 *
 * GOOL is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, version 3.
 *
 * GOOL is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License version 3 for more details.
 *
 * You should have received a copy of the GNU General Public License along with GOOL,
 * in the file COPYING.txt.  If not, see <http://www.gnu.org/licenses/>.
 */

package gool.recognizer.cpp.ast.expression;

import gool.recognizer.cpp.visitor.DebugASTCpp;
import gool.recognizer.cpp.visitor.IVisitorASTCpp;
import gool.recognizer.cpp.visitor.DebugASTCpp.EASTstatu;

import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;

/**
 * Represents a node expression(array access) of the C++ AST.
 */
public class ASTCppArraySubscriptExpression extends ASTCppExpression {

	public ASTCppArraySubscriptExpression(IASTNode node) {
		super(node);
		setNode((IASTArraySubscriptExpression) node);
	}

	private IASTArraySubscriptExpression node ;
	
	
	public IASTArraySubscriptExpression getNode() {
		return node;
	}

	public void setNode(IASTArraySubscriptExpression node) {
		this.node = node;
	}
	
	@Override
	public Object accept(IVisitorASTCpp visitor, Object data) {
		DebugASTCpp.getInstance().printAstIfYouWant(EASTstatu.VISIT, "ASTCppArraySubscriptExpression",this);
		Object toReturn = visitor.visit(this, data);
		DebugASTCpp.getInstance().printAstIfYouWant(EASTstatu.LEAVE, "ASTCppArraySubscriptExpression",this);
		return toReturn;
	}
}
