package org.iot.dsa.dslink.dframework;

import org.iot.dsa.dslink.DSRootNode;
import org.iot.dsa.node.DSInfo;
import org.iot.dsa.node.DSNode;

import java.util.LinkedList;

public class DFHelpers {
    //Knobs
    public static final long DEFAULT_PING_DELAY = 50;
    public static final double RECONNECT_DELAY_MULTIPLIER = 1;

    static final String STATUS = "Status";
    static final String RESTART = "Restart";
    static final String STOP = "Stop";
    static final String START = "Start";
    static final String REMOVE = "Remove";
    static final String IS_STOPPED = "Stopped";
    static final String PRINT = "Print";

    public enum DFConnStrat {
        LAZY,
        ACTIVE,
        HYBRID
    }

    public enum DFRefChangeStrat {
        CONSTANT,
        LINEAR,
        EXPONENTIAL
    }

    public enum DFStatus {
        NEW("Unknown"),
        CONNECTED("Connected"),
        FAILED("Failed"),
        STOPPED(IS_STOPPED),
        STOPPED_BYP("Stopped by Parent");

        String display;

        DFStatus(String display) {
            this.display = display;
        }

        @Override
        public String toString() {
            return display;
        }
    }

    public static String getTestingString(DSNode node, boolean inline, boolean verbose) {
        String nodeType = "Unknown";
        if (node instanceof DSRootNode) {
            nodeType = "Root";
        } else if (node instanceof DFConnectionNode) {
            String tabs = (inline) ? "" : "\t";
            nodeType = tabs + "Conn " + node.getName();
        } else if (node instanceof DFDeviceNode) {
            String tabs = (inline) ? "" : "\t\t";
            nodeType = tabs + "Dev " + node.getName();
        } else if (node instanceof DFPointNode) {
            String tabs = (inline) ? "" : "\t\t\t";
            nodeType = tabs + "Point " + node.getName();
        }

        StringBuilder str = new StringBuilder(nodeType);
        str.append(": ");
        LinkedList<DSInfo> nodes = new LinkedList<DSInfo>();
        LinkedList<DSInfo> actions = new LinkedList<DSInfo>();
        LinkedList<DSInfo> values = new LinkedList<DSInfo>();
        for (DSInfo info : node) {
            if (info.isAction() && verbose) {
                actions.add(info);
            } else if (info.isNode()) {
                nodes.add(info);
            } else if (info.isValue()) {
                if (!info.getName().equals("parameters") || verbose) values.add(info);
            }
        }
        boolean first = true;
        for (DSInfo info : actions) {
            if (!first) str.append(", ");
            str.append(info.getName());
            first = false;
        }
        for (DSInfo info : values) {
            if (!first) str.append(", ");
            str.append(info.getName());
            str.append(":");
            str.append(info.getValue());
            first = false;
        }
        for (DSInfo info : nodes) {
            if (inline) str.append(" - ");
            else str.append("\n");
            str.append(getTestingString(info.getNode(), inline, verbose));
        }
        return str.toString();
    }

    public static final String[] animals = { "Aardvark", "Abyssinian", "Affenpinscher", "Akbash", "Akita", "Albatross", "Alligator",
            "Alpaca", "Angelfish", "Ant", "Anteater", "Antelope", "Ape", "Armadillo", "Ass", "Avocet", "Axolotl",
            "Baboon", "Badger", "Balinese", "Bandicoot", "Barb", "Barnacle", "Barracuda", "Bat", "Beagle", "Bear",
            "Beaver", "Bee", "Beetle", "Binturong", "Bird", "Birman", "Bison", "Bloodhound", "Boar", "Bobcat", "Bombay",
            "Bongo", "Bonobo", "Booby", "Budgerigar", "Buffalo", "Bulldog", "Bullfrog", "Burmese", "Butterfly", "Caiman",
            "Camel", "Capybara", "Caracal", "Caribou", "Cassowary", "Cat", "Caterpillar", "Catfish", "Cattle",
            "Centipede", "Chameleon", "Chamois", "Cheetah", "Chicken", "Chihuahua", "Chimpanzee", "Chinchilla",
            "Chinook", "Chipmunk", "Chough", "Cichlid", "Clam", "Coati", "Cobra", "Cockroach", "Cod", "Collie", "Coral",
            "Cormorant", "Cougar", "Cow", "Coyote", "Crab", "Crane", "Crocodile", "Crow", "Curlew", "Cuscus",
            "Cuttlefish", "Dachshund", "Dalmatian", "Deer", "Dhole", "Dingo", "Dinosaur", "Discus", "Dodo", "Dog",
            "Dogfish", "Dolphin", "Donkey", "Dormouse", "Dotterel", "Dove", "Dragonfly", "Drever", "Duck", "Dugong",
            "Dunker", "Dunlin", "Eagle", "Earwig", "Echidna", "Eel", "Eland", "Elephant", "ElephantSeal", "Elk", "Emu",
            "Falcon", "Ferret", "Finch", "Fish", "Flamingo", "Flounder", "Fly", "Fossa", "Fox", "Frigatebird", "Frog",
            "Galago", "Gar", "Gaur", "Gazelle", "Gecko", "Gerbil", "Gharial", "Giant Panda", "Gibbon", "Giraffe", "Gnat",
            "Gnu", "Goat", "Goldfinch", "Goldfish", "Goose", "Gopher", "Gorilla", "Goshawk", "Grasshopper", "Greyhound",
            "Grouse", "Guanaco", "GuineaFowl", "GuineaPig", "Gull", "Guppy", "Hamster", "Hare", "Harrier", "Havanese",
            "Hawk", "Hedgehog", "Heron", "Herring", "Himalayan", "Hippopotamus", "Hornet", "Horse", "Human", "Hummingbird",
            "Hyena", "Ibis", "Iguana", "Impala", "Indri", "Insect", "Jackal", "Jaguar", "Javanese", "Jay", "BlueJay",
            "Jellyfish", "Kakapo", "Kangaroo", "Kingfisher", "Kiwi", "Koala", "KomodoDragon", "Kouprey", "Kudu",
            "Labradoodle", "Ladybird", "Lapwing", "Lark", "Lemming", "Lemur", "Leopard", "Liger", "Lion", "Lionfish",
            "Lizard", "Llama", "Lobster", "Locust", "Loris", "Louse", "Lynx", "Lyrebird", "Macaw", "Magpie", "Mallard",
            "Maltese", "Manatee", "Mandrill", "Markhor", "Marten", "Mastiff", "Mayfly", "Meerkat", "Millipede", "Mink",
            "Mole", "Molly", "Mongoose", "Mongrel", "Monkey", "Moorhen", "Moose", "Mosquito", "Moth", "Mouse", "Mule",
            "Narwhal", "Neanderthal", "Newfoundland", "Newt", "Nightingale", "Numbat", "Ocelot", "Octopus", "Okapi",
            "Olm", "Opossum", "Orangutan", "Oryx", "Ostrich", "Otter", "Owl", "Ox", "Oyster", "Pademelon", "Panther",
            "Parrot", "Partridge", "Peacock", "Peafowl", "Pekingese", "Pelican", "Penguin", "Persian", "Pheasant", "Pig",
            "Pigeon", "Pika", "Pike", "Piranha", "Platypus", "Pointer", "Pony", "Poodle", "Porcupine", "Porpoise",
            "Possum", "Prairie Dog", "Prawn", "Puffin", "Pug", "Puma", "Quail", "Quelea", "Quetzal", "Quokka", "Quoll",
            "Rabbit", "Raccoon", "Ragdoll", "Rail", "Ram", "Rat", "Rattlesnake", "Raven", "Red deer", "Red panda",
            "Reindeer", "Rhinoceros", "Robin", "Rook", "Rottweiler", "Ruff", "Salamander", "Salmon", "SandDollar",
            "Sandpiper", "Saola", "Sardine", "Scorpion", "SeaLion", "SeaUrchin", "Seahorse", "Seal", "Serval", "Shark",
            "Sheep", "Shrew", "Shrimp", "Siamese", "Siberian", "Skunk", "Sloth", "Snail", "Snake", "Snowshoe", "Somali",
            "Sparrow", "Spider", "Sponge", "Squid", "Squirrel", "Starfish", "Starling", "Stingray", "Stinkbug", "Stoat",
            "Stork", "Swallow", "Swan", "Tang", "Tapir", "Tarsier", "Termite", "Tetra", "Tiffany", "Tiger", "Toad",
            "Tortoise", "Toucan", "Tropicbird", "Trout", "Tuatara", "Turkey", "Turtle", "Uakari", "Uguisu", "Umbrellabird",
            "Vicuna", "Viper", "Vulture", "Wallaby", "Walrus", "Warthog", "Wasp", "WaterBuffalo", "Weasel", "Whale",
            "Whippet", "Wildebeest", "Wolf", "Wolverine", "Wombat", "Woodcock", "Woodlouse", "Woodpecker", "Worm",
            "Wrasse", "Wren", "Yak", "Zebra", "Zebu", "Zonkey", "Zorse" };

    public static final String[] places = {"Inselberg", "Monadnock", "Hill", "Knob", "Ridge", "Mountain", "Peak", "Stack", "Mesa",
            "Butte", "Escarpment", "Gorge", "Seacliff", "Rivercliff", "Stonerun", "Crag", "Tor", "Promontory"};

    public static final String[] colors = {"amber", "amethyst", "apricot", "aqua", "aquamarine", "auburn", "azure", "beige", "black",
            "blue", "bronze", "brown", "buff", "burntUmber", "cardinal", "carmine", "celadon", "cerise", "cerulean",
            "charcoal", "chartreuse", "chocolate", "cinnamon", "color", "complementary", "copper", "coral", "cream",
            "crimson", "cyan", "dark", "denim", "desertSand", "ebony", "ecru", "eggplant", "emerald", "forestGreen",
            "fuchsia", "gold", "goldenrod", "gray", "green", "grey", "hotPink", "hue", "indigo", "ivory", "jade", "jet",
            "jungleGreen", "kellyGreen", "khaki", "lavender", "lemon", "light", "lilac", "lime", "magenta", "mahogany",
            "maroon", "mauve", "mustard", "navyBlue", "ocher", "olive", "orange", "orchid", "pale", "pastel", "peach",
            "periwinkle", "persimmon", "pewter", "pink", "primary", "puce", "pumpkin", "purple", "rainbow", "red",
            "rose", "ruby", "russet", "rust", "saffron", "salmon", "sapphire", "scarlet", "seaGreen", "secondary",
            "sepia", "shade", "shamrock", "sienna", "silver", "spectrum", "slate", "steelBlue", "tan", "tangerine",
            "taupe", "teal", "terracotta", "thistle", "tint", "tomato", "topaz", "turquoise", "ultramarine", "umber",
            "vermilion", "violet", "viridian", "wheat", "white", "wisteria", "yellow"};

    public static final String[] parts = {"ankle", "arch", "arm", "armpit", "beard", "breast", "calf", "cheek", "chest", "chin",
            "earlobe", "elbow", "eyebrow", "eyelash", "eyelid", "face", "finger", "forearm", "forehead", "gum", "heel",
            "hip", "indexFinger", "jaw", "knee", "knuckle", "leg", "lip", "mouth", "mustache", "nail", "neck", "nostril",
            "palm", "pinkie", "pupil", "scalp", "shin", "shoulder", "sideburns", "thigh", "throat", "thumb",
            "tongue", "tooth", "waist", "wrist"};

    public static final String[] adjectives = {"aback", "abaft", "abandoned", "abashed", "aberrant", "abhorrent", "abiding",
            "abject", "ablaze", "able", "abnormal", "aboard", "aboriginal", "abortive", "abounding", "abrasive",
            "abrupt", "absent", "absolute", "absorbed", "absorbing", "abstracted", "absurd", "abundant", "abusive",
            "academic", "acceptable", "accessible", "accidental", "acclaimed", "accomplished", "accurate", "aching",
            "acid", "acidic", "acoustic", "acrid", "acrobatic", "active", "actual", "actually", "adHoc", "adamant",
            "adaptable", "addicted", "adept", "adhesive", "adjoining", "admirable", "admired", "adolescent", "adorable",
            "adored", "advanced", "adventurous", "affectionate", "afraid", "aged", "aggravating", "aggressive", "agile",
            "agitated", "agonizing", "agreeable", "ahead", "ajar", "alarmed", "alarming", "alcoholic", "alert",
            "alienated", "alike", "alive", "all", "alleged", "alluring", "aloof", "altruistic", "amazing", "ambiguous",
            "ambitious", "amiable", "ample", "amuck", "amused", "amusing", "anchored", "ancient", "ancient", "angelic",
            "angry", "angry", "anguished", "animated", "annoyed", "annoying", "annual", "another", "antique", "antsy",
            "anxious", "any", "apathetic", "appetizing", "apprehensive", "appropriate", "apt", "aquatic", "arctic",
            "arid", "aromatic", "arrogant", "artistic", "ashamed", "aspiring", "assorted", "assured", "astonishing",
            "athletic", "attached", "attentive", "attractive", "auspicious", "austere", "authentic", "authorized",
            "automatic", "available", "avaricious", "average", "awake", "aware", "awesome", "awful", "awkward",
            "axiomatic", "babyish", "back", "bad", "baggy", "barbarous", "bare", "barren", "bashful", "basic", "batty",
            "bawdy", "beautiful", "beefy", "befitting", "belated", "belligerent", "beloved", "beneficial", "bent",
            "berserk", "best", "better", "bewildered", "bewitched", "big", "big", "billowy", "biodegradable", "bite",
            "biting", "bitter", "bizarre", "black", "black", "bland", "blank", "blaring", "bleak", "blind", "blissful",
            "blond", "bloody", "blue", "blue", "blushing", "bogus", "boiling", "bold", "bony", "boorish", "bored",
            "boring", "bossy", "both", "bouncy", "boundless", "bountiful", "bowed", "brainy", "brash", "brave", "brawny",
            "breakable", "breezy", "brief", "bright", "brilliant", "brisk", "broad", "broken", "bronze", "brown",
            "bruised", "bubbly", "bulky", "bumpy", "buoyant", "burdensome", "burly", "bustling", "busy", "buttery",
            "buzzing", "cagey", "calculating", "callous", "calm", "candid", "canine", "capable", "capital", "capricious",
            "carefree", "careful", "careless", "caring", "cautious", "cavernous", "ceaseless", "celebrated", "certain",
            "changeable", "charming", "cheap", "cheeky", "cheerful", "cheery", "chemical", "chief", "childlike", "chilly",
            "chivalrous", "chubby", "chunky", "circular", "clammy", "classic", "classy", "clean", "clear", "clear",
            "clever", "cloistered", "close", "closed", "cloudy", "clueless", "clumsy", "cluttered", "coarse", "coherent",
            "cold", "colorful", "colorless", "colossal", "colossal", "combative", "comfortable", "common", "compassionate",
            "competent", "complete", "complex", "complicated", "composed", "concerned", "concrete", "condemned",
            "condescending", "confused", "conscious", "considerate", "constant", "contemplative", "content", "conventional",
            "convincing", "convoluted", "cooing", "cooked", "cool", "cooperative", "coordinated", "corny", "corrupt",
            "costly", "courageous", "courteous", "cowardly", "crabby", "crafty", "craven", "crazy", "creamy", "creative",
            "creepy", "criminal", "crisp", "critical", "crooked", "crowded", "cruel", "crushing", "cuddly", "cultivated",
            "cultured", "cumbersome", "curious", "curly", "curved", "curvy", "cut", "cute", "cylindrical", "cynical",
            "daffy", "daily", "damaged", "damaging", "damp", "dangerous", "dapper", "dapper", "daring", "dark", "darling",
            "dashing", "dazzling", "dead", "deadly", "deadpan", "deafening", "dear", "dearest", "debonair", "decayed",
            "deceitful", "decent", "decimal", "decisive", "decorous", "deep", "deeply", "defeated", "defective",
            "defenseless", "defensive", "defiant", "deficient", "definite", "delayed", "delectable", "delicate",
            "delicious", "delightful", "delirious", "demanding", "demonic", "dense", "dental", "dependable", "dependent",
            "depraved", "depressed", "deranged", "descriptive", "deserted", "despicable", "detailed", "determined",
            "devilish", "devoted", "didactic", "different", "difficult", "digital", "dilapidated", "diligent", "dim",
            "diminutive", "dimpled", "dimwitted", "direct", "direful", "dirty", "disagreeable", "disastrous", "discreet",
            "discrete", "disfigured", "disguised", "disgusted", "disgusting", "dishonest", "disillusioned", "disloyal",
            "dismal", "dispensable", "distant", "distinct", "distorted", "distraught", "distressed", "disturbed",
            "divergent", "dizzy", "domineering", "dopey", "doting", "double", "doubtful", "downright", "drab",
            "draconian", "drafty", "drained", "dramatic", "dreary", "droopy", "drunk", "dry", "dual", "dull", "dull",
            "dusty", "dutiful", "dynamic", "dysfunctional", "each", "eager", "early", "earnest", "earsplitting",
            "earthy", "easy", "easy", "eatable", "economic", "ecstatic", "edible", "educated", "efficacious",
            "efficient", "eight", "elaborate", "elastic", "elated", "elderly", "electric", "elegant", "elementary",
            "elfin", "elite", "elliptical", "emaciated", "embarrassed", "embellished", "eminent", "emotional", "empty",
            "enchanted", "enchanting", "encouraging", "endurable", "energetic", "enlightened", "enormous", "enraged",
            "entertaining", "enthusiastic", "entire", "envious", "envious", "equable", "equal", "equatorial", "erect",
            "erratic", "essential", "esteemed", "ethereal", "ethical", "euphoric", "evanescent", "evasive", "even",
            "evergreen", "everlasting", "every", "evil", "exalted", "exasperated", "excellent", "excitable", "excited",
            "exciting", "exclusive", "exemplary", "exhausted", "exhilarated", "exotic", "expensive", "experienced",
            "expert", "extensive", "extra", "extraneous", "extra", "extroverted", "exuberant", "exultant", "fabulous",
            "faded", "failing", "faint", "fair", "faithful", "fake", "fallacious", "false", "familiar", "famous",
            "fanatical", "fancy", "fantastic", "far", "faraway", "far", "far", "fascinated", "fast", "fat", "fatal",
            "fatherly", "faulty", "favorable", "favorite", "fearful", "fearless", "feeble", "feigned", "feisty",
            "feline", "female", "feminine", "fertile", "festive", "few", "fickle", "fierce", "filthy", "fine", "finicky",
            "finished", "firm", "first", "firsthand", "fitting", "five", "fixed", "flagrant", "flaky", "flamboyant",
            "flashy", "flat", "flawed", "flawless", "flickering", "flimsy", "flippant", "floppy", "flowery", "flufy",
            "fluid", "flustered", "fluttering", "foamy", "focused", "fond", "foolhardy", "foolish", "forceful",
            "foregoing", "forgetful", "forked", "formal", "forsaken", "forthright", "fortunate", "four", "fragile",
            "fragrant", "frail", "frank", "frantic", "frayed", "free", "freezing", "French", "frequent", "fresh",
            "fretful", "friendly", "frightened", "frightening", "frigid", "frilly", "frivolous", "frizzy", "front",
            "frosty", "frothy", "frozen", "frugal", "fruitful", "frustrating", "full", "fumbling", "fumbling",
            "functional", "funny", "furry", "furtive", "fussy", "future", "futuristic", "fuzzy", "gabby", "gainful",
            "gamy", "gaping", "gargantuan", "garrulous", "gaseous", "gaudy", "general", "general", "generous", "gentle",
            "genuine", "ghastly", "giant", "giddy", "gifted", "gigantic", "giving", "glamorous", "glaring", "glass",
            "gleaming", "gleeful", "glib", "glistening", "glittering", "gloomy", "glorious", "glossy", "glum", "godly",
            "golden", "good", "good", "goofy", "gorgeous", "graceful", "gracious", "grand", "grandiose", "grandiose",
            "granular", "grateful", "gratis", "grave", "gray", "greasy", "great", "greedy", "green", "gregarious",
            "grey", "grieving", "grim", "grimy", "gripping", "grizzled", "groovy", "gross", "grotesque", "grouchy",
            "grounded", "growing", "growling", "grown", "grubby", "gruesome", "grumpy", "guarded", "guiltless", "guilty",
            "gullible", "gummy", "gusty", "guttural", "habitual", "hairy", "half", "half", "hallowed", "halting",
            "handmade", "handsome", "handsomely", "handy", "hanging", "hapless", "happy", "happyGoLucky", "hard",
            "hardToFind", "harebrained", "harmful", "harmless", "harmonious", "harsh", "hasty", "hateful", "haunting",
            "heady", "healthy", "heartbreaking", "heartfelt", "hearty", "heavenly", "heavy", "hefty", "hellish",
            "helpful", "helpless", "hesitant", "hidden", "hideous", "high", "highfalutin", "highLevel", "highPitched",
            "hilarious", "hissing", "historical", "hoarse", "holistic", "hollow", "homeless", "homely", "honest",
            "honorable", "honored", "hopeful", "horrible", "horrific", "hospitable", "hot", "huge", "hulking", "humble",
            "humdrum", "humiliating", "humming", "humongous", "humorous", "hungry", "hurried", "hurt", "hurtful",
            "hushed", "husky", "hypnotic", "hysterical", "icky", "icy", "ideal", "ideal", "idealistic", "identical",
            "idiotic", "idle", "idolized", "ignorant", "ill", "illegal", "illFated", "illInformed", "illiterate",
            "illustrious", "imaginary", "imaginative", "immaculate", "immaterial", "immediate", "immense", "imminent",
            "impartial", "impassioned", "impeccable", "imperfect", "imperturbable", "impish", "impolite", "important",
            "imported", "impossible", "impractical", "impressionable", "impressive", "improbable", "impure", "inborn",
            "incandescent", "incomparable", "incompatible", "incompetent", "incomplete", "inconclusive",
            "inconsequential", "incredible", "indelible", "indolent", "industrious", "inexpensive", "inexperienced",
            "infamous", "infantile", "infatuated", "inferior", "infinite", "informal", "innate", "innocent",
            "inquisitive", "insecure", "insidious", "insignificant", "insistent", "instinctive", "instructive",
            "insubstantial", "intelligent", "intent", "intentional", "interesting", "internal", "international",
            "intrepid", "intrigued", "invincible", "irate", "ironclad", "irresponsible", "irritable", "irritating",
            "itchy", "jaded", "jagged", "jam", "jaunty", "jazzy", "jealous", "jittery", "jobless", "joint", "jolly",
            "jovial", "joyful", "joyous", "jubilant", "judicious", "juicy", "jumbled", "jumbo", "jumpy", "jumpy",
            "junior", "juvenile", "kaleidoscopic", "kaput", "keen", "key", "kind", "kindhearted", "kindly", "klutzy",
            "knobby", "knotty", "knowing", "knowledgeable", "known", "kooky", "kosher", "labored", "lackadaisical",
            "lacking", "lame", "lame", "lamentable", "languid", "lanky", "large", "last", "lasting", "late", "laughable",
            "lavish", "lawful", "lazy", "leading", "leafy", "lean", "learned", "left", "legal", "legitimate", "lethal",
            "level", "lewd", "light", "lighthearted", "likable", "like", "likeable", "likely", "limited", "limp",
            "limping", "linear", "lined", "liquid", "literate", "little", "live", "lively", "livid", "living",
            "loathsome", "lone", "lonely", "long", "longing", "long", "loose", "lopsided", "lost", "loud", "loutish",
            "lovable", "lovely", "loving", "low", "lowly", "loyal", "lucky", "ludicrous", "lumbering", "luminous",
            "lumpy", "lush", "lustrous", "luxuriant", "luxurious", "lying", "lyrical", "macabre", "macho", "mad",
            "maddening", "made", "madly", "magenta", "magical", "magnificent", "majestic", "major", "makeshift",
            "male", "malicious", "mammoth", "maniacal", "many", "marked", "married", "marvelous", "masculine",
            "massive", "material", "materialistic", "mature", "meager", "mealy", "mean", "measly", "meaty", "medical",
            "mediocre", "medium", "meek", "melancholy", "mellow", "melodic", "melted", "memorable", "menacing",
            "merciful", "mere", "merry", "messy", "metallic", "mighty", "mild", "military", "milky", "mindless",
            "miniature", "minor", "minty", "minute", "miscreant", "miserable", "miserly", "misguided", "mistaken",
            "misty", "mixed", "moaning", "modern", "modest", "moist", "moldy", "momentous", "monstrous", "monthly",
            "monumental", "moody", "moral", "mortified", "motherly", "motionless", "mountainous", "muddled", "muddy",
            "muffled", "multicolored", "mundane", "mundane", "murky", "mushy", "musty", "mute", "muted", "mysterious",
            "naive", "nappy", "narrow", "nasty", "natural", "naughty", "nauseating", "nautical", "near", "neat",
            "nebulous", "necessary", "needless", "needy", "negative", "neglected", "negligible", "neighboring",
            "neighborly", "nervous", "nervous", "new", "next", "nice", "nice", "nifty", "nimble", "nine", "nippy",
            "nocturnal", "noiseless", "noisy", "nonchalant", "nondescript", "nonsensical", "nonstop", "normal",
            "nostalgic", "nosy", "notable", "noted", "noteworthy", "novel", "noxious", "null", "numb", "numberless",
            "numerous", "nutritious", "nutty", "oafish", "obedient", "obeisant", "obese", "oblivious", "oblong",
            "obnoxious", "obscene", "obsequious", "observant", "obsolete", "obtainable", "obvious", "occasional",
            "oceanic", "odd", "oddball", "offbeat", "offensive", "official", "oily", "old", "oldFashioned", "omniscient",
            "one", "onerous", "only", "open", "opposite", "optimal", "optimistic", "opulent", "orange", "orderly",
            "ordinary", "organic", "original", "ornate", "ornery", "ossified", "other", "our", "outgoing", "outlandish",
            "outlying", "outrageous", "outstanding", "oval", "overconfident", "overcooked", "overdue", "overjoyed",
            "overlooked", "overrated", "overt", "overwrought", "painful", "painstaking", "palatable", "pale", "paltry",
            "panicky", "panoramic", "parallel", "parched", "parsimonious", "partial", "passionate", "past", "pastel",
            "pastoral", "pathetic", "peaceful", "penitent", "peppery", "perfect", "perfumed", "periodic", "perky",
            "permissible", "perpetual", "perplexed", "personal", "pertinent", "pesky", "pessimistic", "petite", "petty",
            "petty", "phobic", "phony", "physical", "picayune", "piercing", "pink", "piquant", "pitiful", "placid",
            "plain", "plaintive", "plant", "plastic", "plausible", "playful", "pleasant", "pleased", "pleasing", "plucky",
            "plump", "plush", "pointed", "pointless", "poised", "polished", "polite", "political", "pompous", "poor",
            "popular", "portly", "posh", "positive", "possessive", "possible", "potable", "powerful", "powerless",
            "practical", "precious", "premium", "present", "present", "prestigious", "pretty", "previous", "pricey",
            "prickly", "primary", "prime", "pristine", "private", "prize", "probable", "productive", "profitable",
            "profuse", "proper", "protective", "proud", "prudent", "psychedelic", "psychotic", "public", "puffy",
            "pumped", "punctual", "pungent", "puny", "pure", "purple", "purring", "pushy", "pushy", "putrid", "puzzled",
            "puzzling", "quack", "quaint", "quaint", "qualified", "quarrelsome", "quarterly", "queasy", "querulous",
            "questionable", "quick", "quickest", "quickWitted", "quiet", "quintessential", "quirky", "quixotic",
            "quixotic", "quizzical", "rabid", "racial", "radiant", "ragged", "rainy", "rambunctious", "rampant",
            "rapid", "rare", "rash", "raspy", "ratty", "raw", "ready", "real", "realistic", "reasonable", "rebel",
            "recent", "receptive", "reckless", "recondite", "rectangular", "red", "redundant", "reflecting", "reflective",
            "regal", "regular", "reliable", "relieved", "remarkable", "reminiscent", "remorseful", "remote", "repentant",
            "repulsive", "required", "resolute", "resonant", "respectful", "responsible", "responsive", "revolving",
            "rewarding", "rhetorical", "rich", "right", "righteous", "rightful", "rigid", "ringed", "ripe", "ritzy",
            "roasted", "robust", "romantic", "roomy", "rosy", "rotating", "rotten", "rotund", "rough", "round", "rowdy",
            "royal", "rubbery", "ruddy", "rude", "rundown", "runny", "rural", "rustic", "rusty", "ruthless", "sable",
            "sad", "safe", "salty", "same", "sandy", "sane", "sarcastic", "sardonic", "sassy", "satisfied", "satisfying",
            "savory", "scaly", "scandalous", "scant", "scarce", "scared", "scary", "scattered", "scented", "scholarly",
            "scientific", "scintillating", "scornful", "scratchy", "scrawny", "screeching", "second", "secondary",
            "second", "secret", "secretive", "sedate", "seemly", "selective", "selfAssured", "selfish", "selfReliant",
            "sentimental", "separate", "serene", "serious", "serpentine", "several", "severe", "shabby", "shadowy",
            "shady", "shaggy", "shaky", "shallow", "shameful", "shameless", "sharp", "shimmering", "shiny", "shivering",
            "shocked", "shocking", "shoddy", "short", "shortTerm", "showy", "shrill", "shut", "shy", "sick", "silent",
            "silky", "silly", "silver", "similar", "simple", "simplistic", "sincere", "sinful", "single", "six",
            "sizzling", "skeletal", "skillful", "skinny", "sleepy", "slight", "slim", "slimy", "slippery", "sloppy",
            "slow", "slushy", "small", "smarmy", "smart", "smelly", "smiling", "smoggy", "smooth", "smug", "snappy",
            "snarling", "sneaky", "sniveling", "snobbish", "snoopy", "snotty", "sociable", "soft", "soggy", "solid",
            "somber", "some", "sophisticated", "sordid", "sore", "sorrowful", "soulful", "soupy", "sour", "sour",
            "Spanish", "sparkling", "sparse", "special", "specific", "spectacular", "speedy", "spherical", "spicy",
            "spiffy", "spiky", "spirited", "spiritual", "spiteful", "splendid", "spooky", "spotless", "spotted",
            "spotty", "spry", "spurious", "squalid", "square", "squeaky", "squealing", "squeamish", "squiggly", "stable",
            "staid", "stained", "staking", "stale", "standard", "standing", "starchy", "stark", "starry", "statuesque",
            "steadfast", "steady", "steel", "steep", "stereotyped", "sticky", "stiff", "stimulating", "stingy", "stormy",
            "stout", "straight", "strange", "strict", "strident", "striking", "striped", "strong", "studious", "stunning",
            "stunning", "stupendous", "stupid", "sturdy", "stylish", "subdued", "submissive", "subsequent", "substantial",
            "subtle", "suburban", "successful", "succinct", "succulent", "sudden", "sugary", "sulky", "sunny", "super",
            "superb", "superficial", "superior", "supportive", "supreme", "sure", "surprised", "suspicious", "svelte",
            "swanky", "sweaty", "sweet", "sweltering", "swift", "sympathetic", "symptomatic", "synonymous", "taboo",
            "tacit", "tacky", "talented", "talkative", "tall", "tame", "tan", "tangible", "tangy", "tart", "tasteful",
            "tasteless", "tasty", "tattered", "taut", "tawdry", "tearful", "tedious", "teeming", "teeny", "teeny",
            "telling", "temporary", "tempting", "ten", "tender", "tense", "tenuous", "tepid", "terrible", "terrific",
            "tested", "testy", "thankful", "that", "therapeutic", "these", "thick", "thin", "thinkable", "third",
            "thirsty", "this", "thorny", "thorough", "those", "thoughtful", "thoughtless", "threadbare", "threatening",
            "three", "thrifty", "thundering", "thunderous", "tidy", "tight", "tightfisted", "timely", "tinted", "tiny",
            "tired", "tiresome", "toothsome", "torn", "torpid", "total", "tough", "towering", "tragic", "trained",
            "tranquil", "trashy", "traumatic", "treasured", "tremendous", "triangular", "tricky", "trifling", "trim",
            "trite", "trivial", "troubled", "truculent", "true", "trusting", "trustworthy", "trusty", "truthful",
            "tubby", "turbulent", "twin", "two", "typical", "ubiquitous", "ugliest", "ugly", "ultimate", "ultra",
            "unable", "unaccountable", "unarmed", "unaware", "unbecoming", "unbiased", "uncomfortable", "uncommon",
            "unconscious", "uncovered", "understated", "understood", "undesirable", "unequal", "unequaled", "uneven",
            "unfinished", "unfit", "unfolded", "unfortunate", "unhappy", "unhealthy", "uniform", "unimportant",
            "uninterested", "unique", "united", "unkempt", "unknown", "unlawful", "unlined", "unlucky", "unnatural",
            "unpleasant", "unrealistic", "unripe", "unruly", "unselfish", "unsightly", "unsteady", "unsuitable",
            "unsung", "untidy", "untimely", "untried", "untrue", "unused", "unusual", "unwelcome", "unwieldy",
            "unwitting", "unwritten", "upbeat", "uppity", "upright", "upset", "uptight", "urban", "usable", "used",
            "used", "useful", "useless", "utilized", "utopian", "utter", "uttermost", "vacant", "vacuous", "vagabond",
            "vague", "vain", "valid", "valuable", "vapid", "variable", "various", "vast", "velvety", "venerated",
            "vengeful", "venomous", "verdant", "verifiable", "versed", "vexed", "vibrant", "vicious", "victorious",
            "vigilant", "vigorous", "villainous", "violent", "violet", "virtual", "virtuous", "visible", "vital",
            "vivacious", "vivid", "voiceless", "volatile", "voluminous", "voracious", "vulgar", "wacky", "waggish",
            "waiting", "wakeful", "wan", "wandering", "wanting", "warlike", "warm", "warmhearted", "warped", "wary",
            "wasteful", "watchful", "waterlogged", "watery", "wavy", "weak", "wealthy", "weary", "webbed", "wee",
            "weekly", "weepy", "weighty", "weird", "welcome", "wellDocumented", "wellGroomed", "wellInformed", "wellLit",
            "wellMade", "wellOff", "wellToDo", "wellWorn", "wet", "which", "whimsical", "whirlwind", "whispered",
            "whispering", "white", "whole", "wholesale", "whopping", "wicked", "wide", "wideEyed", "wiggly", "wild",
            "willing", "wilted", "winding", "windy", "winged", "wiry", "wise", "wistful", "witty", "wobbly", "woebegone",
            "woeful", "womanly", "wonderful", "wooden", "woozy", "wordy", "workable", "worldly", "worn", "worried",
            "worrisome", "worse", "worst", "worthless", "worthwhile", "worthy", "wrathful", "wretched", "writhing",
            "wrong", "wry", "XandY", "xenophobic", "yawning", "yearly", "yellow", "yellowish", "yielding", "young",
            "youthful", "yummy", "zany", "zealous", "zesty", "zigzag", "zippy", "zonked"};
}