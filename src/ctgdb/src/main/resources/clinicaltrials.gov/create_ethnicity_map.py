
infile = "ethnicities.txt"
fh = open(infile, 'r')
for line in fh.readlines():
    e = line.lower().strip()
    c = ""
    
    # Multiple races
    if "biracial" in e or "multi" in e or "more than" in e or "mixed" in e or "mulatto" in e or "two or more" in e or "mutli" in e or "bi-racial" in e:
        c = "multiple/biracial"
    else:
        if "alaska" in e:
            c = "native american"
        else:
            if e.startswith("cauc") or e.startswith("white") or "caucasian" in e and "not of hisp" in e:
                c = "caucasian"
            else:
            
                if "back" in e or "black" in e or "afirican" in e or "africa" in e or "afro" in e and "not" not in e:
                    c = "black"
                else:
                    if "orient" in e or "asia" in e or "chinese" in e or "japanese" in e or "korea" in e or "malay" in e or "filip" in e or "philip" in e or "taiwan" in e and "cauc" not in e:
                        c = "asian"
                    else:
                        if ("latin" in e or "hispanic" in e or "puerto" in e or "mexi" in e or "brown" in e)  and "not" not in e:
                            c = "hispanic"
                        else:
                            if "latino" in e and "not" in e:
                                c = "caucasian"
                            else:
                                if "finn" in e or "caucassian" in e or "irish" in e or "russia" in e or "jewish" in e or "white" in e or "european" in e or "france" in e or "not hispanic" in e or "caucasion" in e:
                                    c = "caucasian"
                                else:
                                    if "aborigi" in e or "native" in e and ("canad" in e or "indian" in e or "americ" in e):
                                        c = "native american"
                                    else:
                                        if "india" in e:
                                            c = "indian"
                                        else:
                                            if "persia" in e or "arab" in e or "egypt" in e or "lebane" in e or "turk" in e or "middle east" in e:
                                                c = "middle eastern"
                                            else:
                                                if "pacific" in e or "hawaii" in e or "islander" in e:
                                                    c = "native hawaiian"
                                                else:
                                                    if "american" in e:
                                                        c = "native american"
    # final check
    if len(c) == 0:
        if "unable" in e or "prefer not" in e or "unspeci" in e or "not spec" in e or "not collect" in e or "not state" in e or "not report" in e or "not allow" in e or "not avail" in e or "missing" in e or "no data" in e or "declined" in e or "other" in e or "not allowed" in e or "refuse" in e or "unkn" in e or "not answer" in e or "not appli" in e or "not permit" in e:
            c = "other"

    if len(c) > 0:                            
        print( e + "\t" + c )
        #pass
    else:
        print(e + "\t" + "other")
