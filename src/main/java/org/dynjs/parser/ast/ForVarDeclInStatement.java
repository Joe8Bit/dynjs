/**
 *  Copyright 2012 Douglas Campos, and individual contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.dynjs.parser.ast;

import me.qmx.jitescript.CodeBlock;

import org.antlr.runtime.tree.Tree;
import org.dynjs.parser.Statement;
import org.dynjs.runtime.BlockManager;

public class ForVarDeclInStatement extends AbstractForInStatement {

    private final VariableDeclarationStatement decl;

    public ForVarDeclInStatement(final Tree tree, final BlockManager blockManager, final VariableDeclarationStatement decl, final Expression rhs, final Statement block) {
        super(tree, blockManager, rhs, block);
        this.decl = decl;
    }

    @Override
    public CodeBlock getFirstChunkCodeBlock() {
        return decl.getCodeBlock();
    }
    
    public String toIndentedString(String indent) {
        StringBuffer buf = new StringBuffer();
        buf.append( indent ).append( "for (").append( decl.toIndentedString("") ).append( " in " ).append( getRhs().toString() ).append( ") {\n");
        buf.append( getBlock().toIndentedString( indent + "  " ) );
        buf.append( indent ).append( "}" );
        return buf.toString();
    }

}