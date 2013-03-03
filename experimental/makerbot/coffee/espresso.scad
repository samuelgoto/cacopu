$fn = 50;

// all();

// head();
// tube();
scale(0.25)
  parts();

module parts() {
  translate([0, 100, 0]) piston();
  translate([0, -100, 0]) translate([0, 0, 50]) rotate([0, 180, 0]) tube();
  translate([100, 0, 0]) translate([0, 0, 30]) rotate([0, 180, 0]) head();
  holder();
}


module piston() {
  cylinder(h = 10, r = 25);
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
  difference() {
    cylinder(h = 30, r = 40);
    union () {
      translate([0, 0, -10]) holder();
      translate([0, 0, -20]) holder();
      rotate([0, 0, 30]) translate([0, 0, -10]) holder();
      rotate([0, 0, 45]) translate([0, 0, -10]) holder();
    }
    cylinder(h = 30, r = 30);
  }
}

module all() {
  % head();
  rotate([0, 0, 45]) translate([0, 0, -10]) holder();
  % translate([0, 0, 20]) tube();
  translate([0, 0, 50]) piston();
}

module holder() {
  difference() {
    union() {
      cylinder(h = 30, r = 30);
      translate([0, 0, 20]) intersection() {
        cylinder(h = 10, r = 35);
        translate([-10, -35, 0]) cube([20, 70, 10]);
      }
      //cylinder(h = 10, r = 35);
    }
    translate([0, 0, 5]) cylinder(h = 30, r = 25);
    cylinder(h = 5, r = 5);
  }
}
