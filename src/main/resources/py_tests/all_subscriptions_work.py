#from output_parser import *

fail_boat = "Subscription attempt failed:"
addprefix = "Invoking /main/"
addsuffix = "Add"
goodstatus = "Status:Connected"
name_string = "\"Name\":\""
tr = Tracker()

steps = parse("testing-output.txt")
for i in range(0, len(steps)):
    step = steps[i]
    if step.action.startswith(addprefix):
        path = step.action[len(addprefix):].strip().split("/")
        if path[-1].startswith(addsuffix):
            #Cleanup name of last node
            tr.main_test(True, i)
    else:
        tr.side_test(not step.action.startswith(fail_boat), i)

tr.report()