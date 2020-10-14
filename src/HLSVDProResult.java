public class HLSVDProResult {

//            (int), number of singular values found (nsv_found <= nsv_sought)
//            (ndarray, floats) the singular values
//            (ndarray, floats) the frequencies (in kilohertz)
//            (ndarray, floats) the damping factors (in milliseconds?)
//            (ndarray, floats) the amplitudes (in arbitrary units)
//            (ndarray, floats) the phases (in degrees)

    int nsv_found;
    float[] singular_values;
    float[] frequencies;
    float[] damping_factors;
    float[] amplitudes;
    float[] phases;



}
