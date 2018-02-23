# from output_parser import*

subprefix = "Subscribing to /main/"
goodstatus = "Status:Connected"
fails = []
tr = Tracker()

steps = parse("testing-output.txt")
for i in range(0, len(steps)):
    step = steps[i]
    if step.action.startswith(subprefix):
        path = step.action[len(subprefix):].strip().split("/")
        tr.side_test(len(path) == 3, i)
        dsapoint = find_in_dsa_tree(step.dsa_tree, path)
        devpoint = find_in_dev_tree(step.dev_tree, path)
        tr.side_test(dsapoint is not None, i)
        if devpoint is not None and goodstatus in dsapoint.parent.value and goodstatus in dsapoint.parent.parent.value:
            tr.side_test(goodstatus in dsapoint.value, i)
            actual_val = devpoint.value.split()[-1].strip()
            tr.main_test("Value:" + actual_val in dsapoint.value, i)

tr.report()