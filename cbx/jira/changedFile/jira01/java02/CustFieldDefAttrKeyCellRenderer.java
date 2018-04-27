// Copyright (c) 1998-2017 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.5.0.1
// ============================================================================
// CHANGE LOG
// CNT.5.0.1 : 2017-XX-XX, arvin.zheng, creation
// ============================================================================
package com.core.cbx.custfielddef.form;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.core.cbx.data.def.CustomFieldDefConstants;
import com.core.cbx.ui.zk.cul.grid.BaseColumn;
import com.core.cbx.ui.zk.cul.grid.renderer.TextCellRenderer;
import com.core.cbx.ui.zk.util.FieldValueGetter;


/**
 * @author arvin.zheng
 */
public class CustFieldDefAttrKeyCellRenderer extends TextCellRenderer {

    /**
     *
     */
    private static final long serialVersionUID = -5800179638949116927L;

    /* Custom field render attributes for Codelist type */
    public static final String ATTR_RENDER_BOOK_NAME = "Book Name";

    public static final String ATTR_RENDER_MATCH_DATE_FIELD_ID = "Match Date Field ID";

    public static final String ATTR_RENDER_CASCADE = "Cascade";

    public static final String ATTR_RENDER_CASCADE_EXPRESSION = "Cascade Expression";

    /* Custom field render attributes for HclGroup type */
    public static final String ATTR_RENDER_HCL_DATA = "HCL Data";

    public static final String ATTR_RENDER_DISABLE_SSL = "disable SSL";

    public static final Map<String,Object> attrMap = new HashMap<String,Object>();
    static {
        attrMap.put(CustomFieldDefConstants.ATTR_BOOK_NAME, ATTR_RENDER_BOOK_NAME);
        attrMap.put(CustomFieldDefConstants.ATTR_MATCH_DATE_FIELD_ID, ATTR_RENDER_MATCH_DATE_FIELD_ID);
        attrMap.put(CustomFieldDefConstants.ATTR_CASCADE, ATTR_RENDER_CASCADE);
        attrMap.put(CustomFieldDefConstants.ATTR_CASCADE_EXPRESSION, ATTR_RENDER_CASCADE_EXPRESSION);
        attrMap.put(CustomFieldDefConstants.ATTR_HCL_DATA, ATTR_RENDER_HCL_DATA);
        attrMap.put(CustomFieldDefConstants.ATTR_DISABLE_SSL, ATTR_RENDER_DISABLE_SSL);
    }
    /*
     * (non-Javadoc)
     * @see com.core.cbx.ui.zk.cul.grid.renderer.TextCellRenderer#render(org.zkoss.zul.Cell,
     * com.core.cbx.ui.zk.cul.grid.BaseColumn, java.lang.Object)
     */
//    @Override
//    public void render(final Cell cell, final BaseColumn col, final Object data) {
//
//
//        final BaseGrid grid = (BaseGrid) ((Row) cell.getParent()).getGrid();
//        final boolean readonly = grid.isReadonly() || col.isReadonly() || isCustomizedReadonly(cell, col, data);
//        if (readonly) {
//            this.renderLabel(cell, col, data, CbxLabelRenderer.DEFAULT_RENDERER);
//            return;
//        }
//       // super.render(cell, col, data);
//    }



//    @Override
//    protected void renderLabel(final Cell cell, final BaseColumn col, final Object data, final String renderer) {
//        final BaseGrid grid = (BaseGrid) ((Row) cell.getParent()).getGrid();
//        final GenericComposer composer = (GenericComposer) grid.getAttribute(UiConstants.ATTR_COMPOSER);
//        final Object value = getFieldValue(col, data);
//        /*if (value != null && value instanceof String) {
//            value = getStatusName(col.getId(), (String) value, data, grid);
//        }*/
//        final CbxLabel label = new CbxLabel(value, col.getFormat(),
//                renderer, composer, col.getComboKey(), col.getStore());
//        if (col.getMaxlength() != null) {
//            label.setMaxlength(col.getMaxlength().intValue());
//        }
//        cell.appendChild(label);
//    }

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    protected Object getFieldValue(final BaseColumn col, final Object data) {


        final String dataIndex = StringUtils.defaultIfBlank(col.getMapping(), col.getId());
        if(attrMap.containsKey(FieldValueGetter.getEntityFieldValue((Map<String, Object>) data, dataIndex))){

              return attrMap.get(FieldValueGetter.getEntityFieldValue((Map<String, Object>) data, dataIndex));

        }else{

              return FieldValueGetter.getEntityFieldValue((Map<String, Object>) data, dataIndex);
        }

    }

}
