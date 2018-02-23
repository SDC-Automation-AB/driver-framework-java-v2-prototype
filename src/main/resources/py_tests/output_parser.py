

class Step:
    def __init__(self):
        self.action = None
        self.dev_tree = TreeNode(None, None)
        self.dsa_tree = TreeNode(None, None)

class TreeNode:
    def __init__(self, parent, line):
        self.children = []
        self.parent = parent
        self.value = line

    def append(self, line):
        if line.startswith("\t"):
            assert len(self.children) > 0
            self.children[-1].append(line[1:])
        else:
            self.children.append(TreeNode(self, line))

class Tracker:
    def __init__(self):
        self.fails = []
        self.success = []

    def side_test(self, cond, itr):
        if not cond:
            self.fails.append(itr)

    def main_test(self, cond, itr):
        if cond:
            self.success.append(itr)
        else:
            self.fails.append(itr)

    def report(self):
        if len(self.fails) != 0:
            print "Steps failed:"
            print self.fails
            assert False
        elif len(self.success) == 0:
            print "Test case absent from file!"
            assert False
        else:
            print "Successful cases:", len(self.success)
            print self.success


# parses the file into a list of steps
# each step cantains an aciont, a device tree and a dsa tree
def parse(filename):
    f = file(filename)
    steps = []
    step = Step()
    dev_done = False
    dsa_done = False
    for line in f:
        if step.action is None:
            step.action = line
        elif not dev_done:
            if len(line.strip()) == 0:
                dev_done = True
            else:
                step.dev_tree.append(line)
        elif not dsa_done:
            if (len(line.strip())) == 0:
                dsa_done = True
            else:
                step.dsa_tree.append(line)
        else:
            assert line.startswith("== ") and line.endswith("=====\n")
            steps.append(step)
            step = Step()
            dev_done = False
            dsa_done = False
    return steps

#takes a list of strings specifying a path
#returens a dsa sub-tree descibed by the path, if it exists
def find_in_dsa_tree(tree, path):
    assert len(path) <= 3
    assert len(tree.children) == 1
    root = tree.children[0]
    assert root.value == "Root: \n"
    if len(path) == 0:
        return root
    for conn in root.children:
        if conn.value.startswith("Conn " + path[0]):
            if len(path) == 1:
                return conn
            for dev in conn.children:
                if dev.value.startswith("Dev " + path[1]):
                    if len(path) == 2:
                        return dev
                    for point in dev.children:
                        if point.value.startswith("Point " + path[2]):
                            return point
    return None

#takes a list of strings specifying a path
#returens a device sub-tree descibed by the path, if it exists
def find_in_dev_tree(tree, path):
    assert len(path) <= 3
    if len(path) == 0:
        return tree
    for conn in tree.children:
        if conn.value.startswith(path[0]):
            if len(path) == 1:
                return conn
            for dev in conn.children:
                if dev.value.startswith(path[1]):
                    if len(path) == 2:
                        return dev
                    for point in dev.children:
                        if point.value.startswith(path[2]):
                            return point
    return None

#returns a list of dsa point sub-trees contained in a given tree
def get_all_dsa_points(tree):
    assert len(tree.children) == 1
    root = tree.children[0]
    assert root.value == "Root: \n"
    points = []
    for conn in root.children:
        for dev in conn.children:
            points += dev.children
    return points

#returns a list of dsa dev sub-trees contained in a given tree
def get_all_dsa_devs(tree):
    assert len(tree.children) == 1
    root = tree.children[0]
    assert root.value == "Root: \n"
    devs = []
    for conn in root.children:
        devs += conn.children
    return devs