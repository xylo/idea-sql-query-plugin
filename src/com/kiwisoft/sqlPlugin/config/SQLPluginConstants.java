/*
 * Copyright (C) 2002-2006 Stefan Stiller
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.kiwisoft.sqlPlugin.config;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 17:59:52 $
 */
public interface SQLPluginConstants
{
	static final String GENERAL="general";
	static final String CONFIRM_COMMIT="confirmCommit";
	static final String CONFIRM_ROLLBACK="confirmRollback";
	static final String CONFIRM_DISCONNECT="confirmClose";
	static final String SAVE_PASSWORDS="savePasswords";
	static final String ENCODE_PASSWORDS="encodePasswords";
	static final String KEEP_CONNECTIONS_OPEN="keepConnectionsOpen";

	static final String DATABASES="databases";

	static final String RESULTS="results";
	static final String RESIZE_COLUMNS="resizeColumns";
	static final String TO_HEADER="toHeader";
	static final String TO_CONTENT="toContent";
	static final String NULL_STRING="nullString";
	static final String HIGHLIGHT_KEY_COLUMNS="primaryKeyCols";
	static final String PRIMARY_KEY_COLOR="primaryKeyColor";
	static final String FOREIGN_KEY_COLOR="foreignKeyColor";
	static final String ROW_LIMIT="rowLimit";
	static final String DEFAULT_FORMAT="defaultFormat";
	static final String CLASS="class";
	static final String FORMAT="format";
	static final String SAVE_TABLE_CONFIG="saveTableConfig";
	static final String LOAD_LOBS="loadLOBs";
	static final String USE_ALTERNATE_ROW_COLORS="useAlternateRowColors";
	static final String ALTERNATE_ROW_BACKGROUND="alternateRowBackground";
	static final String ALTERNATE_ROW_FOREGROUND="alternateRowForeground";
	static final String SHOW_GRID="showGrid";

	static final String QUERIES="queries";
	static final String SAVE="save";
	static final String PATH="path";
	static final String PARTIAL_EXECUTE="partialExecute";
	static final String PROJECT_CLASS_LOADER="projectClassLoader";
	static final String STOP_ON_ERROR="stopOnError";

	static final String EXPORT="export";

	static final String DRIVERS="drivers";
	static final String DRIVER="driver";
	static final String URL="url";

	static final String TABLES="tables";

	static final String VALUE="value";
	static final String ENABLED="enabled";
}
