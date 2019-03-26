// Generated by delombok at Sun Mar 24 19:34:08 CDT 2019
package com.eden.orchid.api.theme.menus;

import com.eden.orchid.api.OrchidContext;
import com.eden.orchid.api.options.annotations.AllOptions;
import com.eden.orchid.api.options.annotations.Description;
import com.eden.orchid.api.options.annotations.IntDefault;
import com.eden.orchid.api.options.annotations.Option;
import com.eden.orchid.api.registration.Prioritized;
import com.eden.orchid.api.server.annotations.Extensible;
import com.eden.orchid.api.theme.components.ModularPageListItem;
import com.eden.orchid.api.theme.pages.OrchidPage;
import java.util.List;
import java.util.Map;

/**
 * @orchidApi extensible
 * @since v1.0.0
 */
@Extensible
@Description(value = "A factory that produces dynamic menu items.", name = "Menu Items")
public abstract class OrchidMenuFactory extends Prioritized implements ModularPageListItem<OrchidMenu, OrchidMenuFactory> {
    protected final OrchidContext context;
    protected final String type;
    protected OrchidPage page;
    @Option
    @IntDefault(0)
    @Description("By default, menu items are rendered in the order in which they are declared, but the ordering can be changed by setting the order on any individual menu item. A higher value for order will render that menu item earlier in the list.")
    protected int order;
    @AllOptions
    private Map<String, Object> allData;
    @Option
    @Description("Set all the menu items from this as a dropdown, instead of including them directly at the root.")
    protected boolean asSubmenu;
    @Option
    @Description("The title the menu")
    protected String submenuTitle;

    public OrchidMenuFactory(OrchidContext context, String type, int priority) {
        super(priority);
        this.type = type;
        this.context = context;
    }

    public boolean canBeUsedOnPage(OrchidPage containingPage, OrchidMenu menu, List<Map<String, Object>> possibleMenuItems, List<OrchidMenuFactory> currentMenuItems) {
        return true;
    }

    public abstract List<MenuItem> getMenuItems();

    public String getType() {
        return this.type;
    }

    public int getOrder() {
        return this.order;
    }

    public void setPage(OrchidPage page) {
        this.page = page;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @java.lang.SuppressWarnings("all")
    public Map<String, Object> getAllData() {
        return this.allData;
    }

    @java.lang.SuppressWarnings("all")
    public void setAllData(final Map<String, Object> allData) {
        this.allData = allData;
    }

    @java.lang.SuppressWarnings("all")
    public boolean isAsSubmenu() {
        return this.asSubmenu;
    }

    @java.lang.SuppressWarnings("all")
    public void setAsSubmenu(final boolean asSubmenu) {
        this.asSubmenu = asSubmenu;
    }

    @java.lang.SuppressWarnings("all")
    public String getSubmenuTitle() {
        return this.submenuTitle;
    }

    @java.lang.SuppressWarnings("all")
    public void setSubmenuTitle(final String submenuTitle) {
        this.submenuTitle = submenuTitle;
    }
}
