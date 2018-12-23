package buaa.jj.complier.SyntaxAnalysis;

import buaa.jj.complier.LexicalAnalyse.Identifier;
import javafx.util.Pair;

import java.util.*;

public class IdentifierList {
    private int variableAvaliAddress = 0;
    private int constantAvaliAddress = 0;
    private int functionAvaliAddress = 0;
    private Stack<Identifier> list = new Stack<Identifier>();
    private Stack<Integer> level = new Stack<Integer>();

    public void init() {
        level.push(0);
    }

    public Pair<Identifier,Integer> findIdentifier(String name) {
        int layer = 0;
        ListIterator<Identifier> i = list.listIterator(list.size());
        while (i.hasPrevious()) {
            Identifier tmp = i.previous();
            if (tmp.name.equals(name)) {
                int pos = list.size() - list.search(tmp);
                ListIterator<Integer> j = level.listIterator(level.size());
                while (j.hasPrevious()) {
                    Integer tmp1 = j.previous();
                    if (j.hasPrevious()) {
                        if (tmp1 > pos) {
                            layer++;
                        } else {
                            return new Pair<>(tmp,layer);
                        }
                    } else {
                        return new Pair<>(tmp,layer);
                    }
                }
            }
        }
        return null;
    }

    public boolean insertIdentifier(Identifier identifier,boolean type) {
        int i = level.peek();
        List<Identifier> subList = list.subList(i,list.size());
        for (Identifier id : subList) {
            if (id.name.equals(identifier.name)) {
                return false;
            }
        }
        if (identifier.type == 0) {
            identifier.address = variableAvaliAddress++ * 4;
        } else if (identifier.type == 1) {
            identifier.address = constantAvaliAddress++ * 4 + 400;
        } else if (identifier.type == 2) {
            identifier.address = functionAvaliAddress++ * 4 + 800;
        }
        if (type) {
            list.insertElementAt(identifier,level.peek());
        } else {
            list.push(identifier);
        }
        return true;
    }

    public boolean changeIdentifier(String name, Identifier identifier) {
        Pair<Identifier,Integer> tmp1 = findIdentifier(name);
        Identifier tmp = tmp1.getKey();
        if (tmp == null) {
            return false;
        } else {
            int i = list.indexOf(tmp);
            list.remove(tmp);
            list.insertElementAt(identifier,i);
            return true;
        }
    }

    public void addLevel() {
        level.push(list.size());
    }

    public void removeLevel() {
        int i = list.size() - level.pop() - 1;
        for (int j = 0; j < i; j++) {
            list.pop();
        }
    }
}
