# from output_parser import*

subprefix = "Subscribing to /main/"
goodstatus = "Status:Connected"

steps = parse("testing-output.txt")
for i in range(0, len(steps)):
    step = steps[i]
    if step.action.startswith(subprefix):
        path = step.action[len(subprefix):].strip().split("/")
        assert len(path) == 3
        dsapoint = find_in_dsa_tree(step.dsa_tree, path)
        devpoint = find_in_dev_tree(step.dev_tree, path)
        assert dsapoint is not None
        if devpoint is not None and goodstatus in dsapoint.parent.value and goodstatus in dsapoint.parent.parent.value:
            assert goodstatus in dsapoint.value
            actual_val = devpoint.value.split()[-1].strip()
            assert "Value:" + actual_val in dsapoint.value