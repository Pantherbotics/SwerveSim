# SwerveSim

This was a test to see if a vector based field-oriented swerve could calculate its odometry using the 4 wheel vectors.

It was a success and was turned into a fun demo as an executable jar file (run install under the maven project).

Vectors aren't realistic to those on the robot and are idealized, but the position odometry functions correctly. The simulation does not simulate the robot rotation, therefore the odometry for rotation is not taken into account, although position should still be accurate.
