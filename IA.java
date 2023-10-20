import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class IA
{

    // les depalcements possibles
    private String[] moves = { "F", "FW", "FS", "FC" };
    private ArrayList<Node> queue;
    private ArrayList<Node> solutions;
    private Node root;

    public IA()
    {
        queue = new ArrayList<Node>();
        solutions = new ArrayList<Node>();

    }
/*La 1ere position c'est a gauche*/

    private class State
    {
        private String bank; // la position du fermier
        private TreeSet<String> left, right; // left et right banks avec ce qui se trouvent la bas

        public State(String bank, TreeSet<String> left, TreeSet<String> right)
        {
            this.bank = bank;
            this.left = left;
            this.right = right;
        }

 /*check avec treeset est ce que les deux qui ce trouvent sur le bank peut etre ensemble ou non*/
        private boolean checkAllowBank(TreeSet<String> b)
        {
            // Wolf and Sheep together without Farmer
            if (b.contains("W") && b.contains("S") && (b.contains("F") == false))
                return false;
            // Sheep and Cabbage together without Farmer
            if (b.contains("S") && b.contains("C") && (b.contains("F") == false))
                return false;

            return true;
        }

        /*check les deux banks*/

        public boolean isAllow()
        {
            if (checkAllowBank(left) && checkAllowBank(right))
                return true;
            else
                return false;
        }

        /*check si la solution est valide ou non */

        public boolean isSolution()
        {
            if (left.isEmpty() && right.contains("W") && right.contains("S") && right.contains("C")
                    && right.contains("F"))
                return true;
            else
                return false;
        }

        /*transition a un nouveau state qui retourne soit la situation soit null si invalide la tansition*/

        public State transits(String move)
        {
            String nbank;
            TreeSet<String> nleft = new TreeSet<String>();
            TreeSet<String> nright = new TreeSet<String>();

            if (bank.equalsIgnoreCase("left"))
                nbank = "right";
            else
                nbank = "left";

            copylist(right, nright);
            copylist(left, nleft);

            for (int i = 0; i < move.length(); i++)
            {
                String item = move.substring(i, i + 1);
                if (bank.equalsIgnoreCase("left"))
                {
                    if (nleft.remove(item))
                        nright.add(item);
                    else
                        return null; // retourn null si les deux sont invalide
                }
                else
                {
                    if (nright.remove(item))
                        nleft.add(item);
                    else
                        return null; 
                }
            }

            return new State(nbank, nleft, nright);

        }


         /* pour mettre la nouvelle state */

        private void copylist(TreeSet<String> src, TreeSet<String> dst)
        {
            for (String e : src)
                dst.add(e);
        }

        /* comparaison des deux states */
        public boolean compare(State s)
        {
            TreeSet<String> tmp;

            if (!s.getBank().equalsIgnoreCase(bank))
                return false;

            tmp = s.getLeft();
            for (String e : left)
            {
                if (!tmp.contains(e))
                    return false;
            }

            tmp = s.getRight();
            for (String e : right)
            {
                if (!tmp.contains(e))
                    return false;
            }

            return true;
        }

        public String getBank()
        {
            return bank;
        }

        public TreeSet<String> getLeft()
        {
            return left;
        }

        public TreeSet<String> getRight()
        {
            return right;
        }

        @Override
        public String toString()
        {
            StringBuffer ret = new StringBuffer();
            ret.append("{L:");

            for (String e : left)
                ret.append(e);

            ret.append(" ");
            ret.append("R:");

            for (String e : right)
                ret.append(e);

            ret.append("}");
            return ret.toString();
        }

    }

    /*construction du graphe*/

    private class Node
    {
        public Node parent; // Parent of the node
        public State data; // State of the node
        public ArrayList<Node> adjlist; // Children of the node
        public int level; // Depth of the node
        public String move; // The move (transition) that creates the current
                            // node state.

        public Node(State data)
        {
            parent = null;
            this.data = data;
            adjlist = new ArrayList<Node>();
            level = 0;
            move = "";
        }

        /**
         * Checks si une node contient une autre ou pas */

        public boolean isAncestor()
        {
            Node n = parent;
            boolean ret = false;
            while (n != null)
            {
                if (data.compare(n.data))
                {
                    ret = true;
                    break;
                }

                n = n.parent;
            }

            return ret;
        }

    }


    public void largeur()
    {
        solutions = new ArrayList<Node>(); 
        TreeSet<String> left = new TreeSet<String>();
        left.add("W");
        left.add("S");
        left.add("C");
        left.add("F");

        State inits = new State("left", left, new TreeSet<String>());
        root = new Node(inits);
        root.level = 0;
        queue.add(root);

        while (!queue.isEmpty())
        {
            Node n = queue.remove(0);
            System.out.println("Niveau" + n.level + " " + n.data); // data is the state
            for (String m : moves)
            {

                State s = n.data.transits(m); // transition qui retourne le nv etat

                if (s != null && s.isAllow()) // if its allowed
                {

                    Node child = new Node(s);
                    child.parent = n;
                    child.level = n.level + 1;
                    child.move = m + " " + child.data.getBank(); // on l'ajoute au graphe


                    // check that it doesnt accure already as a predecesseur
                    if (!child.isAncestor())
                    {
                        n.adjlist.add(child);

                        if (child.data.isSolution() == false)// if its not solution or not
                        {
                            queue.add(child);
                            System.out.println(" State " + child.data); 
                        }
                        else
                        {
                            solutions.add(child);
                            System.out.println("Solution " + child.data);

                        }
                    }

                }

            }

        }
    }


    public void Profondeur()
    {

        int dlimit = 1; 
        solutions = new ArrayList<Node>(); 

        while (solutions.size() == 0 && dlimit <= 10)
        {
            TreeSet<String> left = new TreeSet<String>();
            left.add("W");
            left.add("S");
            left.add("C");
            left.add("F");

            State inits = new State("left", left, new TreeSet<String>());
            root = new Node(inits); // nvlle node
            root.level = 0;

            System.out.println("Profondeur: " + dlimit);
            startProf(dlimit, root);
            dlimit++;
        }

    }

     /* Profondeur d'abord*/
    public void startProf(int depth, Node r)
    {
        if (depth == 0)
        {
            System.out.println("Maximum ");
            return;
        }

        System.out.println("Niveau" + r.level + " " + r.data);

        for (String m : moves) // the one that created the current state
        {
            State s = r.data.transits(m);

            if (s != null && s.isAllow()) 
            {

                Node child = new Node(s);
                child.parent = r;
                child.level = r.level + 1;
                child.move = m + "   " + child.data.getBank();

                if (!child.isAncestor()) 
                {
                    r.adjlist.add(child);

                    if (child.data.isSolution())
                    {

                        solutions.add(child);
                        System.out.println("Solution " + child.data);
                        return;
                    }
                    else
                    {
                        startProf(depth - 1, child); // we go to the other child 
                    }

                }
            }

        }
        return;

    }

    public void printLargeur()
    {
        ArrayList<Node> queue = new ArrayList<Node>();

        queue.add(root);

        while (!queue.isEmpty())
        {
            Node n = queue.remove(0);
            System.out.println("Level " + n.level + " " + n.data);

            ArrayList<Node> adjlist = n.adjlist;
            for (Node e : adjlist)
            {
                queue.add(e);
            }

        }

    }

    public void solution()
    {
        System.out.println("No. of solutions:  " + solutions.size());
        ArrayList<Node> stack;

        Iterator<Node> iter = solutions.iterator(); // for looping in the arraylist
        int i = 1;
        while (iter.hasNext())
        {
            stack = new ArrayList<Node>();
            Node n = iter.next();
            stack.add(n);

            n = n.parent;
            while (n != null)
            {
                stack.add(n);
                n = n.parent;
            }
            System.out.println("Solution " + i);
            printSequence(stack);
            i++;
        }

    }

    private void printSequence(ArrayList<Node> stack)
    {
        StringBuffer buf = new StringBuffer();
        buf.append("No. of moves: ");
        buf.append(stack.size() - 1);
        buf.append("\n");
        for (int i = stack.size() - 1; i >= 0; i--)
        {
            Node n = stack.get(i);
            buf.append(n.data.toString());
            if (i != 0)
            {
                buf.append("--");
                buf.append(stack.get(i - 1).move);
                buf.append("->>");

            }
        }

        System.out.println(buf.toString());

    }

    public static void main(String[] args)
    {
        System.out.println(" Wolf, Sheep, Cabbage, Farmer\n");
        IA obj = new IA();

        System.out.println("Creation d'un graphe par largeur d'abord");
        obj.largeur();

        System.out.println("\n\nState Graphe largeur");
        obj.printLargeur();
        System.out.println("\n\n");

        System.out.println("Solutions ");
        obj.solution();

        System.out.println("\n\nCreation d'un graphe par Profondeur d'abord");
        obj.Profondeur();

        System.out.println("\n\nState Graphe Profondeur");
        obj.printLargeur();
        System.out.println("\n\n");

        System.out.println("Solutions");
        obj.solution();

    }

}