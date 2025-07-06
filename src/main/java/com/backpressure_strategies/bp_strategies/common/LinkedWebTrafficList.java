package com.backpressure_strategies.bp_strategies.common;

import java.util.ArrayList;

import com.backpressure_strategies.bp_strategies.model.WebTraffic;

public class LinkedWebTrafficList {
    
    private WebTrafficNode head;
    private WebTrafficNode tail;

    public LinkedWebTrafficList() {
        head = new WebTrafficNode(new WebTraffic("null","null" , -1));
        tail = head;
    }

    public WebTraffic get(int index) {
      var node = head.getNext();
      int counter = 0;
      while(node != null) {
        if(counter == index) {
            return node.getValue();
        }
        node = node.getNext();
        counter++;
      }
      return null;
    }

    //inserts a web traffic element as the head node (one behind the dummy node)
    public void insertHead(WebTraffic element) {
        var newNode = new WebTrafficNode(element);
        newNode.setNext(head.getNext());
        head.setNext(newNode);
        if(newNode.getNext() == null) {
            tail.setNext(newNode);
        }
    }

    public void inserTail(WebTraffic element) {
        tail.setNext(new WebTrafficNode(element));
        tail = tail.getNext();
    }

    public boolean removeElement(int index) {
        int counter = 0;
        var node = head.getNext();
        while(node != null && counter < index) {
            counter++;
            node = node.getNext();
        }

        if(node != null && node.getNext() != null) {
            if(node.getNext() == tail) {
                tail = node;
            }
            node.setNext(node.getNext().getNext()); 
            return true;
        }
        return false;
    }

    public ArrayList<WebTraffic> getValues() {
        var list = new ArrayList<WebTraffic>();
        var node = head.getNext();
        while(node != null) {
            list.add(node.getValue());
            node = node.getNext();
        }
        return list;
    }
}
