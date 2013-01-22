/* Copyright (C) 2012 cloudbase.io
 
 This program is free software; you can redistribute it and/or modify it under
 the terms of the GNU General Public License, version 2, as published by
 the Free Software Foundation.
 
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 for more details.
 
 You should have received a copy of the GNU General Public License
 along with this program; see the file COPYING.  If not, write to the Free
 Software Foundation, 59 Temple Place - Suite 330, Boston, MA
 02111-1307, USA.
 */
package com.cloudbase.datacommands;


/**
 * The operators used by <strong>CBSearchCondition</strong> objects when executed.
 * @author Stefano Buliani
 *
 */
public enum CBSearchConditionOperator {
	CBOperatorEqual(""),
    CBOperatorLess("$lt"),
    CBOperatorLessOrEqual("$te"),
    CBOperatorBigger("$gt"),
    CBOperatorBiggerOrEqual("$gte"),
    CBOperatorAll("$all"),
    CBOperatorExists("$exists"),
    CBOperatorMod("$mod"),
    CBOperatorNe("$ne"),
    CBOperatorIn("$in"),
    CBOperatorNin("$nin"),
    CBOperatorSize("$size"),
    CBOperatorType("$type"),
    CBOperatorWithin("$within"),
    CBOperatorNear("$near")
    ;	
	
	private CBSearchConditionOperator(final String text) {
        this.text = text;
    }

    private final String text;

    public String toString() {
        // TODO Auto-generated method stub
        return text;
    }
}
