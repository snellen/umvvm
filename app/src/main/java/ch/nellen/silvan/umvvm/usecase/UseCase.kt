package ch.nellen.silvan.umvvm.usecase

/**
 * Interface definition for use cases. A use case is a single purpose, reusable class that contains
 * no state variables. Think of a use case as a building block used to construct the logic of a BaseViewModel.
 *
 * @param InputType the type of the parameter for this use case. Use Unit for use cases that do not take a parameter
 * @param OutputType the type of the result returned when executing this use case.
 */
interface UseCase<InputType, OutputType> {

    /**
     * Executes this use case, using input to produce an output of type OutputType. Execution may
     * suspend if this involves long running operations, such as an API call or processing large
     * amounts of data.
     */
    suspend fun execute(input: InputType): OutputType
}