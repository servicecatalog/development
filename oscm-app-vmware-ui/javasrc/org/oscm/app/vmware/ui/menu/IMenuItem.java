package org.oscm.app.vmware.ui.menu;

public interface IMenuItem {

	String getLabel();

	String getLink();

	int getPosition();

	boolean isEnabled();
}
