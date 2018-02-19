#from output_parser import *

invoke_prefix = "Invoking /main/"
stopsuffix = "Stop"
startsuffix = "Start"
stop_true = "Stopped:true"
stop_false = "Stopped:false"
stop = False
fails = []


def check_correct(node, shold_stop):
    if shold_stop:
        return stop_true in node.value
    else:
        return stop_false in node.value

steps = parse("testing-output.txt")
count_stop = 0
count_start = 0
for i in range(0, len(steps)):
    step = steps[i]
    if step.action.startswith(invoke_prefix):
        path = step.action[len(invoke_prefix):].strip().split("/")

        # Check whether we are dealing with a start or a stop
        if path[-1].startswith(stopsuffix):
            stop = True
            count_stop += 1
        elif path[-1].startswith(startsuffix):
            stop = False
            count_start += 1
        else:
            continue


        path.pop() # Don't need the parameters for this
        node_before = find_in_dsa_tree(steps[i-1].dsa_tree, path)
        node_after = find_in_dsa_tree(step.dsa_tree, path)
        # Check that it was correct before
        assert node_before is not None
        if not check_correct(node_before, not stop):
            fails.append(i)
        # Check that it is correct now
        assert node_after is not None
        if not check_correct(node_after, stop):
            fails.append(i)


if count_stop == 0:
    print("No Stop actions detected!")
    assert False
else:
    print count_stop, "Stop actions detected."

if count_start == 0:
    print "No Start actions detected!"
    assert False
else:
    print count_start, "Start actions detected."

if len(fails) != 0:
    print fails
    assert False