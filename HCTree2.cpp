/****************************************************************************

                                                                          a
                                                          CSE 12, Fall 2014
                                                                          k
                                                                          k
                               Assignment k

File Name:		(file name)
Description:		(enter description here)

****************************************************************************/
//
//  HCTree.cpp
//  A11757007
//
//  Created by Aryaman Sharda on 10/21/14.
//
//
#include "HCTree.hpp"
#include "HCNode.hpp"
#include <queue>
#include <stack>
#include <vector>
#include <iostream>
#include <fstream>
#define ERROR -1

/*---------------------------------------------------------------------------
Function Name:                
Purpose:                      
Description:                      
Input:                      
Output:                       
Result:                      
Side Effects:
---------------------------------------------------------------------------*/
void HCTree::build(const vector<int>& freqs)
{
    //Sets up the priority queue to hold the nodes later on
    priority_queue<HCNode*,std::vector<HCNode*>,HCNodePtrComp> nodeQueue;
    
    //Creates node based off of the symbol frequencies

//TODO
    for (unsigned int x = 0; x < freqs.size(); x++)
    {
        //If the symbol exists, add the node to the priority queue.
        if (freqs[x])
        {
            //Adds a HCNode to the vector array with symbol data
            leaves[x] = new HCNode(freqs[x],x,0,0,0);
            
            //Add the node to the vector
            nodeQueue.push(leaves[x]);
        }
    }
    
    //A special case if there is only 1 symbol in the file.
    if (nodeQueue.size() == 1)
    {
        //Create a temporary node to store the top Node
        HCNode *topNode = nodeQueue.top();
        
        //Create a root node with certain inherited properties
        root = new HCNode(topNode->count, topNode->symbol, topNode, 0, 0);
        
        //Assign whatever the top node's parent pointer is to the root node
        nodeQueue.top()->p = root;
        
        //Remove the top node
        nodeQueue.pop();
        return;
    }
    
    //A special case if there are no symbols in the file.
    if(nodeQueue.size() == 0)
    {
        return;
    }
    
    //If more than 1 HCNode is remaining in the priority queue, execute algorithm
    while (nodeQueue.size() > 1)
    {
        //Get the two nodes with the smallest symbol occurence
        HCNode *left = nodeQueue.top();
        nodeQueue.pop();
        
        HCNode *right = nodeQueue.top();
        nodeQueue.pop();
        
        //Create a parent node with their symbol frequencies and assign them as child nodes
        HCNode *parent = new HCNode((left->count + right->count), left->symbol, left, right,0);
        left->p = parent;
        right->p = parent;
        
        //Add this parent node - with it's leaves - to the tree
        nodeQueue.push(parent);
    }
    
    //Assign the root to the top HCNode in the priority queue
    root = nodeQueue.top();
    nodeQueue.pop();
}

/*---------------------------------------------------------------------------
Function Name:                
Purpose:                      
Description:                      
Input:                      
Output:                       
Result:                      
Side Effects:
---------------------------------------------------------------------------*/
void HCTree::encode(byte symbol, BitOutputStream& out) const
{
    //Look up the node to encode from it's symbol value.

//TODO
    HCNode* encodeNode = leaves[(int)symbol];
    
    //Creates stack to keep track of tree traversal

//TODO
    stack<int> code;
    
    //Start from the Node and work you're way up
    while(encodeNode != root)
    {
        //If the node is the left child
        if (encodeNode == encodeNode->p->c0)
        {
            code.push(0);
        }
        
        //If the node is the right child
        else
        {
            code.push(1);
        }
        
        //Move up the tree by setting the node to encode to it's parent
        encodeNode = encodeNode->p;
    }
    
    while(!code.empty())
    {
        //Write out the contents of the stack in "reverse" order
        out.writeBit(code.top());
        code.pop();
    }
}

/*---------------------------------------------------------------------------
Function Name:                
Purpose:                      
Description:                      
Input:                      
Output:                       
Result:                      
Side Effects:
---------------------------------------------------------------------------*/
int HCTree::decode(BitInputStream& in) const
{
    //Set the initial node to the root
    HCNode* decodeNode = root;
    
    //Stores the byte read

//TODO
    int byte;
    
    //Continue to decode, if the root exists.
    if(!root)
    {
        return -1;
    }
    
    //Empty stack into the output file
    while(1)
    {

//TODO
        byte = in.readBit();
        
        //Iterate until you reach the end of the document or error occurs
        if (byte == ERROR)
        {
            break;
        }
        
        if (byte == 0)
        {
            decodeNode = decodeNode->c0;
            
            //Check if the node has any children, otherwise it's the symbol
            if (!(decodeNode->c0) && !(decodeNode->c1))
            {
                //Return it's symbol value

//TODO
                return ((int)decodeNode->symbol);
            }
        }
        else
        {
            decodeNode = decodeNode->c1;
            
            //Check if the node has any children, otherwise it's the symbol
            if (!(decodeNode->c0) && !(decodeNode->c1))
            {
                //Return it's symbol value

//TODO
                return ((int)decodeNode->symbol);
            }
        }
    }
    return 1;
}

/*---------------------------------------------------------------------------
Function Name:                
Purpose:                      
Description:                      
Input:                      
Output:                       
Result:                      
Side Effects:
---------------------------------------------------------------------------*/
void deleteIndividualNodes(HCNode *nodeToDelete)
{
    //Recursively delete left subtree
    if (nodeToDelete->c0 != nullptr)
    {
        deleteIndividualNodes(nodeToDelete->c0);
    }
    
    //Recursively delete right subtree
    if (nodeToDelete->c1 != nullptr)
    {
        deleteIndividualNodes(nodeToDelete->c1);
    }
    
    //Delete current node
    delete nodeToDelete;
}

/*---------------------------------------------------------------------------
Function Name:                
Purpose:                      
Description:                      
Input:                      
Output:                       
Result:                      
Side Effects:
---------------------------------------------------------------------------*/
HCTree::~HCTree()
{
    //If the root exists, start clearing the nodes
    if (HCTree::root != nullptr)
    {
        //Calls helper method to remove Nodes recursively
        deleteIndividualNodes(HCTree::root);
        
        //Set the size to 0, no more nodes.
        HCTree::root = nullptr;
    }
}
