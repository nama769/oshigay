package org.example;

import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.NetworkInterface;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public class MACTest extends JFrame{
    JPanel contentPane;
    JScrollPane scrollPane = new JScrollPane();
    MyTree tree = new MyTree();
    DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
    NodeRenderer renderer = new NodeRenderer();
    NodeRenderer2 renderer2 = new NodeRenderer2();
    private TreeMouseHandler treeMouseListener = new TreeMouseHandler();

    //Constructor
    public MACTest()
    {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try
        {
            jbInit();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    //Component initialization
    private void jbInit() throws Exception
    {
        tree.setEditable(false);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setExpandsSelectedPaths(true);
        tree.addMouseListener(treeMouseListener);
        tree.setCellRenderer(renderer);
        tree.addTreeExpansionListener(new TreeExpansionListener()
        {
            public void treeCollapsed(TreeExpansionEvent evt)
            {
            }

            public void treeExpanded(TreeExpansionEvent evt)
            {
            }
        });

        contentPane = (JPanel) this.getContentPane();
        this.setTitle("MyFrame");
        scrollPane.getViewport().add(tree, null);
        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    //Overridden so we can exit when window is closed
    protected void processWindowEvent(WindowEvent e)
    {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            System.exit(0);
        }
    }

    public static void main(String[] args)
    {
        MACTest simpleTree = new MACTest();
        simpleTree.setSize(800,600);
        simpleTree.show();
    }

    private class TreeMouseHandler extends MouseAdapter
    {
        public void mousePressed(MouseEvent e)
        {
            if(SwingUtilities.isLeftMouseButton(e) == false)
            {
                return;
            }
            System.out.println("mousePressed");
            tree.setCellRenderer(renderer2);
        }

        public void mouseClicked(MouseEvent e)
        {
            if((SwingUtilities.isLeftMouseButton(e) == false))
            {
                return;
            }
            TreePath[] selectedPaths = tree.getSelectionPaths();
            if((selectedPaths == null) || (selectedPaths.length == 0))
            {
                return;
            }
            if(selectedPaths.length == 1)
            {
                if(e.getClickCount() == 1)
                {
                    System.out.println("mouseClicked: left button * 1 : " + selectedPaths[0]);
                }
                else if(e.getClickCount() == 2)
                {
                    System.out.println("mouseClicked: left button * 2 : " + selectedPaths[0]);
                }
            }
        }
    }

    class MyTree extends JTree
    {
        public MyTree()
        {
            DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("root");
            DefaultMutableTreeNode sub1Node = new DefaultMutableTreeNode("sub1");
            rootNode.add(sub1Node);

            DefaultMutableTreeNode n = new DefaultMutableTreeNode("011");
            sub1Node.add(n);
            n.add(new DefaultMutableTreeNode("111"));
            n.add(new DefaultMutableTreeNode("112"));
            n.add(new DefaultMutableTreeNode("113"));

            n = new DefaultMutableTreeNode("012");
            sub1Node.add(n);
            n.add(new DefaultMutableTreeNode("121"));
            n.add(new DefaultMutableTreeNode("122"));
            n.add(new DefaultMutableTreeNode("123"));

            this.setModel(new DefaultTreeModel(rootNode));
        }
    }


    class NodeRenderer extends DefaultTreeCellRenderer
    {
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            super.getTreeCellRendererComponent(tree, value,
                    selected, expanded,
                    leaf, row, hasFocus);
            if(node.getUserObject().toString().trim().equals("sub1"))
            {
                setForeground(Color.RED);
            }
            return this;
        }
    }

    class NodeRenderer2 extends DefaultTreeCellRenderer
    {
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            super.getTreeCellRendererComponent(tree, value,
                    selected, expanded,
                    leaf, row, hasFocus);
            if(node.getUserObject().toString().trim().equals("sub1"))
            {
                setForeground(Color.RED);
            }
            if(node.getUserObject().toString().trim().equals("111"))
            {
                setForeground(Color.BLUE);
            }
            return this;
        }
    }

}
