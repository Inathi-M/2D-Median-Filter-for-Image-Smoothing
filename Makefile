#Makefile for Assignment 1
#Nkosinathi Tshaphile
#28 February 2022

JAVAC=/usr/bin/javac
.SUFFIXES: .java .class
SRCDIR=src
BINDIR=bin

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<
	
CLASSES=AVLExperiment.class  BinaryTreeNode.class BTQueueNode.class   BTQueue.class BinaryTree.class  BinarySearchTree.class AVLTree.class\
CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)
         


clean: 
	rm $(BINDIR)/*.class
	
runarray: $(CLASS_FILES)
	java -cp $(BINDIR) AVLExperiment