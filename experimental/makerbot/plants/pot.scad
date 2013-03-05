$fn = 100;

// all();

parts(1);

module parts(s) {
  scale(s) {
    translate([80, 0, 0]) base();
    translate([-80, 0, 0]) top();
  }
}

module all() {
  % base();
  translate([0, 0, 130]) rotate([0, 180, 0]) top();
}

module top() {
  thickness = 5;
  offset = 2;
  difference() {
    cylinder(h = 10, r = 40);
    cylinder(h = 10, r = 40 - offset - 2 * thickness);
  }
  translate([0, 0, 10]) difference() {
    cylinder(h = 30, r = 40 - offset - thickness);
    cylinder(h = 30, r = 40 - offset - 2 * thickness);
  }
  translate([0, 0, 40]) difference() {
    cylinder(h = 30, r1 = 40 - offset - thickness, r2 = 10);
    cylinder(h = 30, r1 = 40 - offset - 2 * thickness, r2 = 10 - thickness);
  }
}

module base() {
  thickness = 5;

  difference() {
    cylinder(h = 120, r = 40);
    translate([0, 0, thickness]) cylinder(h = 120, r = 40 - thickness);
    // translate([-40, 0, 60]) rotate([0, 90, 0]) cylinder(h = 80, r = 2);
  }
}

