package org.scilab.modules.gui.utils;

import java.util.Iterator;
import java.util.Set;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.docking.event.DockingListener;
import org.flexdock.docking.floating.frames.FloatingDockingPort;
import org.scilab.modules.gui.bridge.tab.SwingScilabDockablePanel;
import org.scilab.modules.gui.bridge.window.SwingScilabWindow;

public class SciDockingListener implements DockingListener {

    private String associatedScilabWindowId;

    public SciDockingListener() {
        super();
    }

    public void dockingCanceled(DockingEvent e) {
        debug("dockingCanceled");
    }

    public void dockingComplete(DockingEvent e) {
        debug("dockingComplete");
        String newId = null;

        DockingListener[] newListeners = e.getNewDockingPort().getDockingListeners();
        SwingScilabDockablePanel dockedTab = (SwingScilabDockablePanel) e.getDockable();
        if (newListeners.length == 2) {

            newId = ((SciDockingListener) newListeners[1]).getAssociatedWindowId();
        } else {
            Set<Dockable> allDockables = e.getNewDockingPort().getDockables();
            Iterator<Dockable> it =  allDockables.iterator();
            Dockable dock = it.next();

            if (it.hasNext()) { 
                while (e.getDockable() == dock) {
                    dock = it.next();
                }
                newId = ((SwingScilabDockablePanel) dock).getParentWindowId();
            } else {

                DefaultDockingPort dockingPort = ((DefaultDockingPort) e.getOldDockingPort());
                JPanel parentPanel = (JPanel) dockingPort.getParent();
                JLayeredPane parentLayeredPane = (JLayeredPane) parentPanel.getParent();
                JRootPane parentRootPane = (JRootPane) parentLayeredPane.getParent();
                int offsetX = dockingPort.getX() + parentPanel.getX() + parentLayeredPane.getX() + parentRootPane.getX();
                int offsetY = dockingPort.getY() + parentPanel.getY() + parentLayeredPane.getY() + parentRootPane.getY();
                int newX = -offsetX;
                int newY = -offsetY;
                if (e.getNewDockingPort() instanceof FloatingDockingPort) {
                    newX += ((FloatingDockingPort) e.getNewDockingPort()).getParent().getParent().getParent().getX();
                    newY += ((FloatingDockingPort) e.getNewDockingPort()).getParent().getParent().getParent().getY();
                } else {
                    newX += ((DefaultDockingPort) e.getNewDockingPort()).getParent().getParent().getParent().getX();
                    newY += ((DefaultDockingPort) e.getNewDockingPort()).getParent().getParent().getParent().getY();
                }

                SwingScilabWindow newWindow = SwingScilabWindow.createWindow(true);
                newWindow.setPosition(new Position(newX, newY));
                newWindow.setDims(SwingScilabWindow.allScilabWindows.get(associatedScilabWindowId).getDims());
                DockingManager.dock(dockedTab, newWindow.getDockingPort());
                newWindow.setVisible(true);
                newId = newWindow.getId();
            }
        }
        dockedTab.setParentWindowId(newId);
        dockedTab.requestFocus();
    }

    public void dragStarted(DockingEvent e) {

        debug("dragStarted");
    }

    public void dropStarted(DockingEvent e) {

        debug("dropStarted");
    }

    public void undockingComplete(DockingEvent e) {

        debug("undockingComplete");


        if (e.getOldDockingPort().getDockables().isEmpty()) {
            SwingScilabWindow.allScilabWindows.get(associatedScilabWindowId).close();
        }
    }

    public void undockingStarted(DockingEvent e) {

        debug("undockingStarted");
    }

    public void setAssociatedWindowId(String id) {
        this.associatedScilabWindowId = id;
    }

    public String getAssociatedWindowId() {
        return this.associatedScilabWindowId;
    }

    private void debug(String method) {
        System.out.println("["+method+"] on Window "+associatedScilabWindowId);
    }

}