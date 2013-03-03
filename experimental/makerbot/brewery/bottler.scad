$fn=100;

//all();

scale(0.25) {
//  translate([-80, 0, 10]) head();
// scale([0.95, 1.05, 0.95]) claw();
   frame();
}

//scale(0.25) { 
// scale([0.95, 1.05, 0.95]) claw();
//}
// % filling(100);

// see law of cosines
// http://en.wikipedia.org/wiki/Triangle
// c^2 = a^2 + b^2 - 2abcos(y)
function pi() = 3.14159265359;
function triangle_isosceles_side(a, y) =
    sqrt(pow(a, 2) + pow(a, 2) - 2 * a * a * cos(y));

// height of the triangle is
// h ^ 2 = a ^ 2 + (c/2) ^ 2 - 2 * a * (c/2) * cos((180 - y) / 2)

function triangle_isosceles_height(a, y) =
  sqrt(pow(a, 2) + pow(base(a, y) / 2, 2) - 2 * a * (base(a, y) / 2) * cos((180 - y) / 2));

// echo(cos(90));

// 3 facets
// 10 radius
// facets = 3;
// base = triangle_isosceles_side(10, 360 / facets);

/// echo(base);

// y = 360 / facets
// 2 * z + y = 180 => c = (180 - y) / 2
// rotate([0, 0, (180 - 360 / facets) / 2]) 
//  translate([- base / 2, -2, 0]) 
//    cube([base, 2, 2]);

//difference() {
//cylinder(h = 30, r = 60);
//cylinder(h = 50, r = 40);
//}

//translate([30, 0, 40]) cube([30, 30, 30]);


module base() {
  difference() {
    cylinder(h = 20, r = 40);
    // beer bottoms have ~30mm radius
    translate([0, 0, 10]) cylinder(h = 20, r = 32);
  }
  difference() {
    translate([0, 0, -35]) cylinder(h = 35, r = 40);
    rotate([0, 180, 0]) claw();
  }
}

module head() {
  difference() {
    union() {
      translate([0, 0, -10]) difference() {
        cylinder(h = 45, r = 40);
        translate([0, 0, 10]) cylinder(h = 25, r = 40);
        translate([0, 0, 35]) cylinder(h = 10, r = 25);
      }
      difference() {
        cylinder(h = 45, r = 40);
        // the radius of the beer cap is ~27.6
        translate([0, 0, 10]) cylinder(h = 25, r = 13.8);
        translate([0, 0, 35])  cylinder(h = 10, r1 = 13.8, r2 = 30);
      }
    }
    claw();
    // filling tube
    translate([0, 0, -10]) cylinder(h = 60, r = 5);
  }
  // translate([0, 0, -40]) cylinder(h = 40, r = 10);
}


module bottom(height) {
  difference() {
    cylinder(h = height, r = 20);
    cylinder(h = height, r = 15);
  }
}

module top() {
  difference() {
      cylinder(h = height, r = 14);
      cylinder(h = height, r = 6);
  }
}

module filling(height) {
  cylinder(h = height, r = 2);
}

module claw() {
  intersection() {
    union() {
      translate([-25, 30, 0]) cube([50, 10, 25]);
      translate([-25, -40, 0]) cube([50, 10, 25]);
      translate([30, -40, 0]) cube([10, 80, 25]);
    }
    cylinder(h = 25, r = 40);
  }
  difference() {
    translate([0, -40, 0]) cube([100, 80, 25]);
    cylinder(h = 25, r = 40);
    cylinder(h = 25, r = 3);
    translate([90, 30, 0]) cylinder(h = 40, r = 5);
    translate([90, -30, 0]) cylinder(h = 40, r = 5);
  }
  translate([0, 30, 12.5]) sphere(2);
  translate([0, -30, 12.5]) sphere(2);
  translate([100, -10, 0]) {
    difference() { union() {
        cube([150, 20, 25]);
        // translate([50, 10, 0]) cylinder(h = 25, r = 20); 
      }
      // translate([50, 10, 0]) sphere(4);
    }
 }
}

module frame() {
  difference () {
    cylinder(h = 10, r = 230);
    cylinder(h = 10, r = 180);
  }
}


module roller(radius) {
  outer_r = 2 * radius;
  h = radius / 2;
  
  difference() {
    translate([0, 0, -h]) cylinder(h = h, r = outer_r);
    sphere(r = radius);
    translate([outer_r - 5, 0, -h]) cylinder(h = h, r = 2);
    translate([-outer_r + 5, 0, -h]) cylinder(h = h, r = 2);
    translate([0, outer_r - 5, -h]) cylinder(h = h, r = 2);
    translate([0, -outer_r + 5, -h]) cylinder(h = h, r = 2);
  }
}

module all() {
  % translate([-290, 0, -70]) {
    // bottom frame
    frame();
    // upper frame
    translate([0, 0, 200]) frame();

    // grid
    translate([200, 30, 0]) cylinder(h = 200, r = 5);
    translate([200, -30, 0]) cylinder(h = 200, r = 5);
  }



  base();
  rotate([0, 180, 0]) claw();

  translate([0, 0, 100]) rotate([0, 180, 0]) {
    head();
    claw();
  }
}
