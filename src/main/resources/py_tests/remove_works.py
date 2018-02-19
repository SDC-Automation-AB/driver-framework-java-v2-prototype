#from output_parser import *

addprefix = "Invoking /main/"
addsuffix = "Remove"
goodstatus = "Status:Connected"
fails = []

steps = parse("testing-output.txt")
count = 0
for i in range(0, len(steps)):
    step = steps[i]
    if step.action.startswith(addprefix):
        path = step.action[len(addprefix):].strip().split("/")
        if path[-1].startswith(addsuffix):
            count += 1
            path.pop() #Don't need the parameters for this
            node_before = find_in_dsa_tree(steps[i-1].dsa_tree, path)
            node_after = find_in_dsa_tree(step.dsa_tree, path)
            if node_before is None:
                fails.append(i)
            if node_after is not None:
                fails.append(i)

if count == 0:
    print("No Remove actions detected!")
    assert False
else:
    print count, "Remove actions detected."

if len(fails) != 0:
    print "Fails:", fails
    assert False