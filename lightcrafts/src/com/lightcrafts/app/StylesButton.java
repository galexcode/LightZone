/* Copyright (C) 2005-2011 Fabio Riccardi */

package com.lightcrafts.app;

import static com.lightcrafts.app.Locale.LOCALE;
import com.lightcrafts.templates.TemplateDatabase;
import com.lightcrafts.templates.TemplateKey;
import com.lightcrafts.ui.browser.view.AbstractImageBrowser;
import com.lightcrafts.ui.toolkit.IconFactory;
import com.lightcrafts.ui.editor.EditorMode;
import com.lightcrafts.prefs.ApplicationMode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * This button triggers a popup menu of templates, and if a popup item is
 * selected, then triggers batch processing of that style on the current
 * browser selection.
 */
class StylesButton extends BrowserButton {

    private final static Icon Icon=
        IconFactory.createInvertedIcon(StylesButton.class, "styles.png");

    private final static String ToolTip = LOCALE.get("StylesButtonToolTip");

    StylesButton(ComboFrame frame) {
        super(frame, LOCALE.get("StylesButtonText"));
        setIcon(Icon);
        setToolTipText(ToolTip);

        // Show the popup on mouse-pressed, not on action-performed.
        if (! ApplicationMode.isBasicMode()) {
            addMouseListener(
                new MouseAdapter() {
                    public void mousePressed(MouseEvent event) {
                        ComboFrame frame = getComboFrame();
                        AbstractImageBrowser browser = frame.getBrowser();
                        ArrayList<File> files = browser.getSelectedFiles();
                        if (! files.isEmpty()) {
                            showPopup(files);
                        }
                    }
                }
            );
        }
        else {
            ApplicationMode.maybeSetToolTip(this);
        }
    }

    void updateEnabled() {
        if (ApplicationMode.isBasicMode()) {
            setEnabled(false);
        }
        else {
            super.updateEnabled();
        }
    }

    private void showPopup(final ArrayList<File> files) {
        JPopupMenu popup = new JPopupMenu();
        try {
            LinkedHashSet<String> namespaces = getTemplateNamespaces();
            for (String namespace : namespaces) {
                JMenu menu = new JMenu(namespace);
                LinkedList<TemplateKey> keys = getKeysForNamespace(namespace);
                for (final TemplateKey key : keys) {
                    JMenuItem item = new JMenuItem(key.getName());
                    item.addActionListener(
                        new ActionListener() {
                            public void actionPerformed(ActionEvent event) {
                                ComboFrame frame = getComboFrame();
                                File[] array = files.toArray(new File[0]);
                                frame.getEditor().setMode( EditorMode.ARROW );
                                Application.applyTemplate(frame, array, key);
                            }
                        }
                    );
                    menu.add(item);
                }
                popup.add(menu);
            }
        }
        catch (TemplateDatabase.TemplateException e) {
            e.printStackTrace();
            // Just don't show the popup
            return;
        }
        Dimension size = getSize();
        popup.setAutoscrolls(true);
        popup.show(this, 0, size.height);
    }

    private static LinkedHashSet<String> getTemplateNamespaces()
        throws TemplateDatabase.TemplateException
    {
        List<TemplateKey> keys = TemplateDatabase.getTemplateKeys();
        LinkedHashSet<String> namespaces = new LinkedHashSet<String>();
        for (TemplateKey key : keys) {
            String namespace = key.getNamespace();
            namespaces.add(namespace);
        }
        return namespaces;
    }

    private static LinkedList<TemplateKey> getKeysForNamespace(String namespace)
        throws TemplateDatabase.TemplateException
    {
        LinkedList<TemplateKey> matches = new LinkedList<TemplateKey>();
        List<TemplateKey> keys = TemplateDatabase.getTemplateKeys();
        for (TemplateKey key : keys) {
            if (key.getNamespace().equals(namespace)) {
                matches.add(key);
            }
        }
        return matches;
    }
}
