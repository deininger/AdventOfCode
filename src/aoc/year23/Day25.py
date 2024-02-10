import sys
from collections import defaultdict
import networkx as nx

D = open(sys.argv[1]).read().strip()
L = D.split('\n')

g = nx.Graph()

for line in L:
    s,e = line.split(':')
    for y in e.split():
        g.add_edge (s, y)
        # g.add_edge (y, s)


g.remove_edges_from(nx.minimum_edge_cut(g))
a, b = nx.connected_components(g)
print (len(a), len(b), len(a)*len(b)) // 543834


#G = nx.DiGraph()
#for k,vs in E.items():
#    for v in vs:
#        G.add_edge(k,v, capacity=1.0)
#
#for x in E.keys():
#    for y in E.keys():
#        if x!=y:
#            cut_value,partition = nx.minimum_cut(G, x, y) 
#           if cut_value == 3:
#               print(x, y, len(partition[0]), len(partiton[1]), len(partition[0])*len(partition[1]))
