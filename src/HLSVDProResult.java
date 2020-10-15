public class HLSVDProResult {

//            (int), number of singular values found (nsv_found <= nsv_sought)
//            (ndarray, floats) the singular values
//            (ndarray, floats) the frequencies (in kilohertz)
//            (ndarray, floats) the damping factors (in milliseconds?)
//            (ndarray, floats) the amplitudes (in arbitrary units)
//            (ndarray, floats) the phases (in degrees)

    int nsv_found;
    double[] singular_values;
    double[] frequencies;
    double[] damping_factors;
    double[] amplitudes;
    double[] phases;


    public HLSVDProResult(int nsv, double[] singvals, double[] freq, double[] damp, double[] ampl, double[] phas) {
        nsv_found = nsv;
        singular_values = singvals;
        frequencies = freq;
        damping_factors = damp;
        amplitudes = ampl;
        phases = phas;
    }
}
