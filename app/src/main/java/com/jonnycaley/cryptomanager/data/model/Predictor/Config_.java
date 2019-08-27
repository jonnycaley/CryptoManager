
package com.jonnycaley.cryptomanager.data.model.Predictor;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config_ {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("trainable")
    @Expose
    private Boolean trainable;
    @SerializedName("batch_input_shape")
    @Expose
    private List<Object> batchInputShape = null;
    @SerializedName("dtype")
    @Expose
    private String dtype;
    @SerializedName("return_sequences")
    @Expose
    private Boolean returnSequences;
    @SerializedName("return_state")
    @Expose
    private Boolean returnState;
    @SerializedName("go_backwards")
    @Expose
    private Boolean goBackwards;
    @SerializedName("stateful")
    @Expose
    private Boolean stateful;
    @SerializedName("unroll")
    @Expose
    private Boolean unroll;
    @SerializedName("time_major")
    @Expose
    private Boolean timeMajor;
    @SerializedName("units")
    @Expose
    private Integer units;
    @SerializedName("activation")
    @Expose
    private String activation;
    @SerializedName("recurrent_activation")
    @Expose
    private String recurrentActivation;
    @SerializedName("use_bias")
    @Expose
    private Boolean useBias;
    @SerializedName("kernel_initializer")
    @Expose
    private KernelInitializer kernelInitializer;
    @SerializedName("recurrent_initializer")
    @Expose
    private RecurrentInitializer recurrentInitializer;
    @SerializedName("bias_initializer")
    @Expose
    private BiasInitializer biasInitializer;
    @SerializedName("unit_forget_bias")
    @Expose
    private Boolean unitForgetBias;
    @SerializedName("kernel_regularizer")
    @Expose
    private Object kernelRegularizer;
    @SerializedName("recurrent_regularizer")
    @Expose
    private Object recurrentRegularizer;
    @SerializedName("bias_regularizer")
    @Expose
    private Object biasRegularizer;
    @SerializedName("activity_regularizer")
    @Expose
    private Object activityRegularizer;
    @SerializedName("kernel_constraint")
    @Expose
    private Object kernelConstraint;
    @SerializedName("recurrent_constraint")
    @Expose
    private Object recurrentConstraint;
    @SerializedName("bias_constraint")
    @Expose
    private Object biasConstraint;
    @SerializedName("dropout")
    @Expose
    private Double dropout;
    @SerializedName("recurrent_dropout")
    @Expose
    private Double recurrentDropout;
    @SerializedName("implementation")
    @Expose
    private Integer implementation;
    @SerializedName("rate")
    @Expose
    private Double rate;
    @SerializedName("noise_shape")
    @Expose
    private Object noiseShape;
    @SerializedName("seed")
    @Expose
    private Object seed;
    @SerializedName("axis")
    @Expose
    private List<Integer> axis = null;
    @SerializedName("momentum")
    @Expose
    private Double momentum;
    @SerializedName("epsilon")
    @Expose
    private Double epsilon;
    @SerializedName("center")
    @Expose
    private Boolean center;
    @SerializedName("scale")
    @Expose
    private Boolean scale;
    @SerializedName("beta_initializer")
    @Expose
    private BetaInitializer betaInitializer;
    @SerializedName("gamma_initializer")
    @Expose
    private GammaInitializer gammaInitializer;
    @SerializedName("moving_mean_initializer")
    @Expose
    private MovingMeanInitializer movingMeanInitializer;
    @SerializedName("moving_variance_initializer")
    @Expose
    private MovingVarianceInitializer movingVarianceInitializer;
    @SerializedName("beta_regularizer")
    @Expose
    private Object betaRegularizer;
    @SerializedName("gamma_regularizer")
    @Expose
    private Object gammaRegularizer;
    @SerializedName("beta_constraint")
    @Expose
    private Object betaConstraint;
    @SerializedName("gamma_constraint")
    @Expose
    private Object gammaConstraint;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getTrainable() {
        return trainable;
    }

    public void setTrainable(Boolean trainable) {
        this.trainable = trainable;
    }

    public List<Object> getBatchInputShape() {
        return batchInputShape;
    }

    public void setBatchInputShape(List<Object> batchInputShape) {
        this.batchInputShape = batchInputShape;
    }

    public String getDtype() {
        return dtype;
    }

    public void setDtype(String dtype) {
        this.dtype = dtype;
    }

    public Boolean getReturnSequences() {
        return returnSequences;
    }

    public void setReturnSequences(Boolean returnSequences) {
        this.returnSequences = returnSequences;
    }

    public Boolean getReturnState() {
        return returnState;
    }

    public void setReturnState(Boolean returnState) {
        this.returnState = returnState;
    }

    public Boolean getGoBackwards() {
        return goBackwards;
    }

    public void setGoBackwards(Boolean goBackwards) {
        this.goBackwards = goBackwards;
    }

    public Boolean getStateful() {
        return stateful;
    }

    public void setStateful(Boolean stateful) {
        this.stateful = stateful;
    }

    public Boolean getUnroll() {
        return unroll;
    }

    public void setUnroll(Boolean unroll) {
        this.unroll = unroll;
    }

    public Boolean getTimeMajor() {
        return timeMajor;
    }

    public void setTimeMajor(Boolean timeMajor) {
        this.timeMajor = timeMajor;
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public String getRecurrentActivation() {
        return recurrentActivation;
    }

    public void setRecurrentActivation(String recurrentActivation) {
        this.recurrentActivation = recurrentActivation;
    }

    public Boolean getUseBias() {
        return useBias;
    }

    public void setUseBias(Boolean useBias) {
        this.useBias = useBias;
    }

    public KernelInitializer getKernelInitializer() {
        return kernelInitializer;
    }

    public void setKernelInitializer(KernelInitializer kernelInitializer) {
        this.kernelInitializer = kernelInitializer;
    }

    public RecurrentInitializer getRecurrentInitializer() {
        return recurrentInitializer;
    }

    public void setRecurrentInitializer(RecurrentInitializer recurrentInitializer) {
        this.recurrentInitializer = recurrentInitializer;
    }

    public BiasInitializer getBiasInitializer() {
        return biasInitializer;
    }

    public void setBiasInitializer(BiasInitializer biasInitializer) {
        this.biasInitializer = biasInitializer;
    }

    public Boolean getUnitForgetBias() {
        return unitForgetBias;
    }

    public void setUnitForgetBias(Boolean unitForgetBias) {
        this.unitForgetBias = unitForgetBias;
    }

    public Object getKernelRegularizer() {
        return kernelRegularizer;
    }

    public void setKernelRegularizer(Object kernelRegularizer) {
        this.kernelRegularizer = kernelRegularizer;
    }

    public Object getRecurrentRegularizer() {
        return recurrentRegularizer;
    }

    public void setRecurrentRegularizer(Object recurrentRegularizer) {
        this.recurrentRegularizer = recurrentRegularizer;
    }

    public Object getBiasRegularizer() {
        return biasRegularizer;
    }

    public void setBiasRegularizer(Object biasRegularizer) {
        this.biasRegularizer = biasRegularizer;
    }

    public Object getActivityRegularizer() {
        return activityRegularizer;
    }

    public void setActivityRegularizer(Object activityRegularizer) {
        this.activityRegularizer = activityRegularizer;
    }

    public Object getKernelConstraint() {
        return kernelConstraint;
    }

    public void setKernelConstraint(Object kernelConstraint) {
        this.kernelConstraint = kernelConstraint;
    }

    public Object getRecurrentConstraint() {
        return recurrentConstraint;
    }

    public void setRecurrentConstraint(Object recurrentConstraint) {
        this.recurrentConstraint = recurrentConstraint;
    }

    public Object getBiasConstraint() {
        return biasConstraint;
    }

    public void setBiasConstraint(Object biasConstraint) {
        this.biasConstraint = biasConstraint;
    }

    public Double getDropout() {
        return dropout;
    }

    public void setDropout(Double dropout) {
        this.dropout = dropout;
    }

    public Double getRecurrentDropout() {
        return recurrentDropout;
    }

    public void setRecurrentDropout(Double recurrentDropout) {
        this.recurrentDropout = recurrentDropout;
    }

    public Integer getImplementation() {
        return implementation;
    }

    public void setImplementation(Integer implementation) {
        this.implementation = implementation;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Object getNoiseShape() {
        return noiseShape;
    }

    public void setNoiseShape(Object noiseShape) {
        this.noiseShape = noiseShape;
    }

    public Object getSeed() {
        return seed;
    }

    public void setSeed(Object seed) {
        this.seed = seed;
    }

    public List<Integer> getAxis() {
        return axis;
    }

    public void setAxis(List<Integer> axis) {
        this.axis = axis;
    }

    public Double getMomentum() {
        return momentum;
    }

    public void setMomentum(Double momentum) {
        this.momentum = momentum;
    }

    public Double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(Double epsilon) {
        this.epsilon = epsilon;
    }

    public Boolean getCenter() {
        return center;
    }

    public void setCenter(Boolean center) {
        this.center = center;
    }

    public Boolean getScale() {
        return scale;
    }

    public void setScale(Boolean scale) {
        this.scale = scale;
    }

    public BetaInitializer getBetaInitializer() {
        return betaInitializer;
    }

    public void setBetaInitializer(BetaInitializer betaInitializer) {
        this.betaInitializer = betaInitializer;
    }

    public GammaInitializer getGammaInitializer() {
        return gammaInitializer;
    }

    public void setGammaInitializer(GammaInitializer gammaInitializer) {
        this.gammaInitializer = gammaInitializer;
    }

    public MovingMeanInitializer getMovingMeanInitializer() {
        return movingMeanInitializer;
    }

    public void setMovingMeanInitializer(MovingMeanInitializer movingMeanInitializer) {
        this.movingMeanInitializer = movingMeanInitializer;
    }

    public MovingVarianceInitializer getMovingVarianceInitializer() {
        return movingVarianceInitializer;
    }

    public void setMovingVarianceInitializer(MovingVarianceInitializer movingVarianceInitializer) {
        this.movingVarianceInitializer = movingVarianceInitializer;
    }

    public Object getBetaRegularizer() {
        return betaRegularizer;
    }

    public void setBetaRegularizer(Object betaRegularizer) {
        this.betaRegularizer = betaRegularizer;
    }

    public Object getGammaRegularizer() {
        return gammaRegularizer;
    }

    public void setGammaRegularizer(Object gammaRegularizer) {
        this.gammaRegularizer = gammaRegularizer;
    }

    public Object getBetaConstraint() {
        return betaConstraint;
    }

    public void setBetaConstraint(Object betaConstraint) {
        this.betaConstraint = betaConstraint;
    }

    public Object getGammaConstraint() {
        return gammaConstraint;
    }

    public void setGammaConstraint(Object gammaConstraint) {
        this.gammaConstraint = gammaConstraint;
    }

}
