$fn = 100;

// % holder(2);
// holder(0);
// tube();
// % translate([0, 0, 30]) head();
// head();
// all();

// head();
// tube();
// scale(0.25)
parts(0.25);

module parts(s) {
  scale(s) {
    translate([0, 100, 0]) piston();
    translate([0, -100, 0]) translate([0, 0, 50]) rotate([0, 180, 0]) tube();
    translate([100, 0, 0]) translate([0, 0, 30]) rotate([0, 180, 0]) head();
    holder(0);
  }
}


module piston() {
  offset = 2;
  cylinder(h = 10, r = 25 - offset);
  cylinder(h = 70, r = 5);
}

module tube() {
  difference() {
    union() {
      cylinder(h = 50, r = 30);
      translate([0, 0, 10]) cylinder(h = 10, r = 40);
    }
    cylinder(h = 50, r = 25);
  }
  translate([0, 0, 40]) {
    difference() {
      cylinder(h = 10, r = 30);
      cylinder(h = 10, r = 5);
    }
  }
}

module head() {
  offset = 2;
  difference() {
    cylinder(h = 30, r = 40);
    union () {
      translate([0, 0, -10]) holder(offset);
      translate([0, 0, -20]) holder(offset);
      rotate([0, 0, 30]) translate([0, 0, -10]) holder(offset);
      rotate([0, 0, 45]) translate([0, 0, -10]) holder(offset);
    }
    cylinder(h = 30, r = 30 + offset);
  }
}

module all() {
  % head();
  rotate([0, 0, 45]) translate([0, 0, -10]) holder(0);
  % translate([0, 0, 20]) tube();
  translate([0, 0, 50]) piston();
}

module holder(offset) {
  difference() {
    union() {
      cylinder(h = 30, r = 30 + offset);
      translate([0, 0, 20]) intersection() {
        translate([0, 0, -offset]) cylinder(h = 10 + offset, r = 35 + offset);
        translate([-10 - offset, -35 - offset, 0 - offset]) cube([20 + 2 * offset, 70 + 2 * offset, 10 + offset]);
      }
    }
    translate([0, 0, 5]) cylinder(h = 30, r = 25 + offset);
    cylinder(h = 5, r = 5);
  }
}
