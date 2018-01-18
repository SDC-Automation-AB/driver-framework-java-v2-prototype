

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


def get_all_dsa_points(tree):
    assert len(tree.children) == 1
    root = tree.children[0]
    assert root.value == "Root: \n"
    points = []
    for conn in root.children:
        for dev in conn.children:
            points += dev.children
    return points

def get_all_dsa_devs(tree):
    assert len(tree.children) == 1
    root = tree.children[0]
    assert root.value == "Root: \n"
    devs = []
    for conn in root.children:
        devs += conn.children
    return devs