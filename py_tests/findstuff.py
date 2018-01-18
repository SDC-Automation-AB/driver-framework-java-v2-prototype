from output_parser import *

remove_prefix = "Invoking /Nodes/"
remove_suffix = "/Remove with parameters {}"

steps = parse("testing-output.txt")
rem_pts = []
for i in range(0, len(steps)):
    step = steps[i]
    act = step.action.strip()
    if act.startswith(remove_prefix) and act.endswith(remove_suffix):
        path = act[len(remove_prefix):-len(remove_suffix)].split("/")
        if len(path) == 3:
            rem_pts.append(path[2])
            # print str(i) + ") " + act + "  (" + path[2] + ")"
        elif len(path) == 2:
            dev = find_in_dsa_tree(steps[i-1].dsa_tree, path)
            for pt in dev.children:
                rem_pts.append(pt.value.strip().split()[1][:-1])
                #print str(i) + ") " + act + "  (" + pt.value.strip().split()[1][:-1] + ")"
        elif len(path) == 1:
            conn = find_in_dsa_tree(steps[i-1].dsa_tree, path)
            for dev in conn.children:
                for pt in dev.children:
                    rem_pts.append(pt.value.strip().split()[1][:-1])
                    #print str(i) + ") " + act + "  (" + pt.value.strip().split()[1][:-1] + ")"
    elif '/Add Point with parameters {"Name":"' in act:
        for rp in rem_pts:
            if '"Name":"' + rp in act:

                print str(i) + ") " + act
                break


