package edu.depauw.emulator_ide.assembler;

import java.util.HashMap;

import edu.depauw.emulator_ide.common.Position;

public class Token{

    enum Type{
    //keyword types
	STRING,
	NUM,
	DIRECTIVE,
	IDENT,
	LABEL,
	CHAR,

	//Register names
	R0,
	R1,
	R2,
	R3,
	R4,
	R5,
	R6,
	R7,
	R8,
	R9,
	R10,
	R11,
	R12,
	R13,
	R14,
	R15,
	CPSR,
	SPSR, //not used but just here for completeness
	
	//Instructions -- Continues for a while these are sorted by their instruction type
	
	//BX Instruction
	BX,
	BXEQ,
	BXNE,
	BXCS,
	BXCC,
	BXMI,
	BXPL,
	BXVS,
	BXVC,
	BXHI,
	BXLS,
	BXGE,
	BXLT,
	BXGT,
	BXLE,
	BXAL,

	//B/BL Instruction
	B,
	BEQ,
	BNE,
	BCS,
	BCC,
	BMI,
	BPL,
	BVS,
	BVC,
	BHI,
	BLS,
	BGE,
	BLT,
	BGT,
	BLE,
	BAL,
	
	BL,
	BLEQ,
	BLNE,
	BLCS,
	BLCC,
	BLMI,
	BLPL,
	BLVS,
	BLVC,
	BLHI,
	BLLS,
	BLGE,
	BLLT,
	BLGT,
	BLLE,
	BLAL,
	
	//Single op data processsing instruction
	//MOV
	MOV,
	MOVEQ,
	MOVNE,
	MOVCS,
	MOVCC,
	MOVMI,
	MOVPL,
	MOVVS,
	MOVVC,
	MOVHI,
	MOVLS,
	MOVGE,
	MOVLT,
	MOVGT,
	MOVLE,
	MOVAL,
	
	//MOV
	MOVS,
	MOVEQS,
	MOVNES,
	MOVCSS,
	MOVCCS,
	MOVMIS,
	MOVPLS,
	MOVVSS,
	MOVVCS,
	MOVHIS,
	MOVLSS,
	MOVGES,
	MOVLTS,
	MOVGTS,
	MOVLES,
	MOVALS,
	//MVN
	MVN,
	MVNEQ,
	MVNNE,
	MVNCS,
	MVNCC,
	MVNMI,
	MVNPL,
	MVNVS,
	MVNVC,
	MVNHI,
	MVNLS,
	MVNGE,
	MVNLT,
	MVNGT,
	MVNLE,
	MVNAL,
	
	MVNS,
	MVNEQS,
	MVNNES,
	MVNCSS,
	MVNCCS,
	MVNMIS,
	MVNPLS,
	MVNVSS,
	MVNVCS,
	MVNHIS,
	MVNLSS,
	MVNGES,
	MVNLTS,
	MVNGTS,
	MVNLES,
	MVNALS,
	
	//CMP
	CMP,
	CMPEQ,
	CMPNE,
	CMPCS,
	CMPCC,
	CMPMI,
	CMPPL,
	CMPVS,
	CMPVC,
	CMPHI,
	CMPLS,
	CMPGE,
	CMPLT,
	CMPGT,
	CMPLE,
	CMPAL,
	
	//CMN
	CMN,
	CMNEQ,
	CMNNE,
	CMNCS,
	CMNCC,
	CMNMI,
	CMNPL,
	CMNVS,
	CMNVC,
	CMNHI,
	CMNLS,
	CMNGE,
	CMNLT,
	CMNGT,
	CMNLE,
	CMNAL,
	
	//TEQ
	TEQ,
	TEQEQ,
	TEQNE,
	TEQCS,
	TEQCC,
	TEQMI,
	TEQPL,
	TEQVS,
	TEQVC,
	TEQHI,
	TEQLS,
	TEQGE,
	TEQLT,
	TEQGT,
	TEQLE,
	TEQAL,
	
	//TST
	TST,
	TSTEQ,
	TSTNE,
	TSTCS,
	TSTCC,
	TSTMI,
	TSTPL,
	TSTVS,
	TSTVC,
	TSTHI,
	TSTLS,
	TSTGE,
	TSTLT,
	TSTGT,
	TSTLE,
	TSTAL,
	
	//AND
	AND,
	ANDEQ,
	ANDNE,
	ANDCS,
	ANDCC,
	ANDMI,
	ANDPL,
	ANDVS,
	ANDVC,
	ANDHI,
	ANDLS,
	ANDGE,
	ANDLT,
	ANDGT,
	ANDLE,
	ANDAL,
	
	ANDS,
	ANDEQS,
	ANDNES,
	ANDCSS,
	ANDCCS,
	ANDMIS,
	ANDPLS,
	ANDVSS,
	ANDVCS,
	ANDHIS,
	ANDLSS,
	ANDGES,
	ANDLTS,
	ANDGTS,
	ANDLES,
	ANDALS,
	
	//EOR
	EOR,
	EOREQ,
	EORNE,
	EORCS,
	EORCC,
	EORMI,
	EORPL,
	EORVS,
	EORVC,
	EORHI,
	EORLS,
	EORGE,
	EORLT,
	EORGT,
	EORLE,
	EORAL,
	
	EORS,
	EOREQS,
	EORNES,
	EORCSS,
	EORCCS,
	EORMIS,
	EORPLS,
	EORVSS,
	EORVCS,
	EORHIS,
	EORLSS,
	EORGES,
	EORLTS,
	EORGTS,
	EORLES,
	EORALS,
	
	//SUB
	SUB,
	SUBEQ,
	SUBNE,
	SUBCS,
	SUBCC,
	SUBMI,
	SUBPL,
	SUBVS,
	SUBVC,
	SUBHI,
	SUBLS,
	SUBGE,
	SUBLT,
	SUBGT,
	SUBLE,
	SUBAL,
	
	SUBS,
	SUBEQS,
	SUBNES,
	SUBCSS,
	SUBCCS,
	SUBMIS,
	SUBPLS,
	SUBVSS,
	SUBVCS,
	SUBHIS,
	SUBLSS,
	SUBGES,
	SUBLTS,
	SUBGTS,
	SUBLES,
	SUBALS,
	
	//RSB
	RSB,
	RSBEQ,
	RSBNE,
	RSBCS,
	RSBCC,
	RSBMI,
	RSBPL,
	RSBVS,
	RSBVC,
	RSBHI,
	RSBLS,
	RSBGE,
	RSBLT,
	RSBGT,
	RSBLE,
	RSBAL,
	
	RSBS,
	RSBEQS,
	RSBNES,
	RSBCSS,
	RSBCCS,
	RSBMIS,
	RSBPLS,
	RSBVSS,
	RSBVCS,
	RSBHIS,
	RSBLSS,
	RSBGES,
	RSBLTS,
	RSBGTS,
	RSBLES,
	RSBALS,
	
	//ADD
	ADD,
	ADDEQ,
	ADDNE,
	ADDCS,
	ADDCC,
	ADDMI,
	ADDPL,
	ADDVS,
	ADDVC,
	ADDHI,
	ADDLS,
	ADDGE,
	ADDLT,
	ADDGT,
	ADDLE,
	ADDAL,
	
	ADDS,
	ADDEQS,
	ADDNES,
	ADDCSS,
	ADDCCS,
	ADDMIS,
	ADDPLS,
	ADDVSS,
	ADDVCS,
	ADDHIS,
	ADDLSS,
	ADDGES,
	ADDLTS,
	ADDGTS,
	ADDLES,
	ADDALS,
	
	//ADC
	ADC,
	ADCEQ,
	ADCNE,
	ADCCS,
	ADCCC,
	ADCMI,
	ADCPL,
	ADCVS,
	ADCVC,
	ADCHI,
	ADCLS,
	ADCGE,
	ADCLT,
	ADCGT,
	ADCLE,
	ADCAL,

	ADCS,
	ADCEQS,
	ADCNES,
	ADCCSS,
	ADCCCS,
	ADCMIS,
	ADCPLS,
	ADCVSS,
	ADCVCS,
	ADCHIS,
	ADCLSS,
	ADCGES,
	ADCLTS,
	ADCGTS,
	ADCLES,
	ADCALS,
	
	//SBC
	SBC,
	SBCEQ,
	SBCNE,
	SBCCS,
	SBCCC,
	SBCMI,
	SBCPL,
	SBCVS,
	SBCVC,
	SBCHI,
	SBCLS,
	SBCGE,
	SBCLT,
	SBCGT,
	SBCLE,
	SBCAL,
	
	SBCS,
	SBCEQS,
	SBCNES,
	SBCCSS,
	SBCCCS,
	SBCMIS,
	SBCPLS,
	SBCVSS,
	SBCVCS,
	SBCHIS,
	SBCLSS,
	SBCGES,
	SBCLTS,
	SBCGTS,
	SBCLES,
	SBCALS,
	
	//RSC
	RSC,
	RSCEQ,
	RSCNE,
	RSCCS,
	RSCCC,
	RSCMI,
	RSCPL,
	RSCVS,
	RSCVC,
	RSCHI,
	RSCLS,
	RSCGE,
	RSCLT,
	RSCGT,
	RSCLE,
	RSCAL,
	
	RSCS,
	RSCEQS,
	RSCNES,
	RSCCSS,
	RSCCCS,
	RSCMIS,
	RSCPLS,
	RSCVSS,
	RSCVCS,
	RSCHIS,
	RSCLSS,
	RSCGES,
	RSCLTS,
	RSCGTS,
	RSCLES,
	RSCALS,
	
	//ORR
	ORR,
	ORREQ,
	ORRNE,
	ORRCS,
	ORRCC,
	ORRMI,
	ORRPL,
	ORRVS,
	ORRVC,
	ORRHI,
	ORRLS,
	ORRGE,
	ORRLT,
	ORRGT,
	ORRLE,
	ORRAL,
	
	ORRS,
	ORREQS,
	ORRNES,
	ORRCSS,
	ORRCCS,
	ORRMIS,
	ORRPLS,
	ORRVSS,
	ORRVCS,
	ORRHIS,
	ORRLSS,
	ORRGES,
	ORRLTS,
	ORRGTS,
	ORRLES,
	ORRALS,
	
	//BIC
	BIC,
	BICEQ,
	BICNE,
	BICCS,
	BICCC,
	BICMI,
	BICPL,
	BICVS,
	BICVC,
	BICHI,
	BICLS,
	BICGE,
	BICLT,
	BICGT,
	BICLE,
	BICAL,
	
	BICS,
	BICEQS,
	BICNES,
	BICCSS,
	BICCCS,
	BICMIS,
	BICPLS,
	BICVSS,
	BICVCS,
	BICHIS,
	BICLSS,
	BICGES,
	BICLTS,
	BICGTS,
	BICLES,
	BICALS,
	
	//MRS
	MRS,
	MRSEQ,
	MRSNE,
	MRSCS,
	MRSCC,
	MRSMI,
	MRSPL,
	MRSVS,
	MRSVC,
	MRSHI,
	MRSLS,
	MRSGE,
	MRSLT,
	MRSGT,
	MRSLE,
	MRSAL,
	
	//MSR
	MSR,
	MSREQ,
	MSRNE,
	MSRCS,
	MSRCC,
	MSRMI,
	MSRPL,
	MSRVS,
	MSRVC,
	MSRHI,
	MSRLS,
	MSRGE,
	MSRLT,
	MSRGT,
	MSRLE,
	MSRAL,
	
	//MUL
	MUL,
	MULEQ,
	MULNE,
	MULCS,
	MULCC,
	MULMI,
	MULPL,
	MULVS,
	MULVC,
	MULHI,
	MULLS,
	MULGE,
	MULLT,
	MULGT,
	MULLE,
	MULAL,
	
	MULS,
	MULEQS,
	MULNES,
	MULCSS,
	MULCCS,
	MULMIS,
	MULPLS,
	MULVSS,
	MULVCS,
	MULHIS,
	MULLSS,
	MULGES,
	MULLTS,
	MULGTS,
	MULLES,
	MULALS,
	
	//MLA
	MLA,
	MLAEQ,
	MLANE,
	MLACS,
	MLACC,
	MLAMI,
	MLAPL,
	MLAVS,
	MLAVC,
	MLAHI,
	MLALS,
	MLAGE,
	MLALT,
	MLAGT,
	MLALE,
	MLAAL,
	
	MLAS,
	MLAEQS,
	MLANES,
	MLACSS,
	MLACCS,
	MLAMIS,
	MLAPLS,
	MLAVSS,
	MLAVCS,
	MLAHIS,
	MLALSS,
	MLAGES,
	MLALTS,
	MLAGTS,
	MLALES,
	MLAALS,
	
	//MUL
	UMULL,
	UMULLEQ,
	UMULLNE,
	UMULLCS,
	UMULLCC,
	UMULLMI,
	UMULLPL,
	UMULLVS,
	UMULLVC,
	UMULLHI,
	UMULLLS,
	UMULLGE,
	UMULLLT,
	UMULLGT,
	UMULLLE,
	UMULLAL,
	
	UMULLS,
	UMULLEQS,
	UMULLNES,
	UMULLCSS,
	UMULLCCS,
	UMULLMIS,
	UMULLPLS,
	UMULLVSS,
	UMULLVCS,
	UMULLHIS,
	UMULLLSS,
	UMULLGES,
	UMULLLTS,
	UMULLGTS,
	UMULLLES,
	UMULLALS,
	
	SMULL,
	SMULLEQ,
	SMULLNE,
	SMULLCS,
	SMULLCC,
	SMULLMI,
	SMULLPL,
	SMULLVS,
	SMULLVC,
	SMULLHI,
	SMULLLS,
	SMULLGE,
	SMULLLT,
	SMULLGT,
	SMULLLE,
	SMULLAL,
	
	SMULLS,
	SMULLEQS,
	SMULLNES,
	SMULLCSS,
	SMULLCCS,
	SMULLMIS,
	SMULLPLS,
	SMULLVSS,
	SMULLVCS,
	SMULLHIS,
	SMULLLSS,
	SMULLGES,
	SMULLLTS,
	SMULLGTS,
	SMULLLES,
	SMULLALS,
	
	//MLAL
	UMLAL,
	UMLALEQ,
	UMLALNE,
	UMLALCS,
	UMLALCC,
	UMLALMI,
	UMLALPL,
	UMLALVS,
	UMLALVC,
	UMLALHI,
	UMLALLS,
	UMLALGE,
	UMLALLT,
	UMLALGT,
	UMLALLE,
	UMLALAL,
	
	UMLALS,
	UMLALEQS,
	UMLALNES,
	UMLALCSS,
	UMLALCCS,
	UMLALMIS,
	UMLALPLS,
	UMLALVSS,
	UMLALVCS,
	UMLALHIS,
	UMLALLSS,
	UMLALGES,
	UMLALLTS,
	UMLALGTS,
	UMLALLES,
	UMLALALS,
	
	SMLAL,
	SMLALEQ,
	SMLALNE,
	SMLALCS,
	SMLALCC,
	SMLALMI,
	SMLALPL,
	SMLALVS,
	SMLALVC,
	SMLALHI,
	SMLALLS,
	SMLALGE,
	SMLALLT,
	SMLALGT,
	SMLALLE,
	SMLALAL,
	
	SMLALS,
	SMLALEQS,
	SMLALNES,
	SMLALCSS,
	SMLALCCS,
	SMLALMIS,
	SMLALPLS,
	SMLALVSS,
	SMLALVCS,
	SMLALHIS,
	SMLALLSS,
	SMLALGES,
	SMLALLTS,
	SMLALGTS,
	SMLALLES,
	SMLALALS,
	
	//LDR
	LDR,
	LDREQ,
	LDRNE,
	LDRCS,
	LDRCC,
	LDRMI,
	LDRPL,
	LDRVS,
	LDRVC,
	LDRHI,
	LDRLS,
	LDRGE,
	LDRLT,
	LDRGT,
	LDRLE,
	LDRAL,
	
	LDRB,
	LDREQB,
	LDRNEB,
	LDRCSB,
	LDRCCB,
	LDRMIB,
	LDRPLB,
	LDRVSB,
	LDRVCB,
	LDRHIB,
	LDRLSB,
	LDRGEB,
	LDRLTB,
	LDRGTB,
	LDRLEB,
	LDRALB,
	
	LDRT,
	LDREQT,
	LDRNET,
	LDRCST,
	LDRCCT,
	LDRMIT,
	LDRPLT,
	LDRVST,
	LDRVCT,
	LDRHIT,
	LDRLST,
	LDRGET,
	LDRLTT,
	LDRGTT,
	LDRLET,
	LDRALT,
	
	LDRBT,
	LDREQBT,
	LDRNEBT,
	LDRCSBT,
	LDRCCBT,
	LDRMIBT,
	LDRPLBT,
	LDRVSBT,
	LDRVCBT,
	LDRHIBT,
	LDRLSBT,
	LDRGEBT,
	LDRLTBT,
	LDRGTBT,
	LDRLEBT,
	LDRALBT,
	
	//STR
	STR,
	STREQ,
	STRNE,
	STRCS,
	STRCC,
	STRMI,
	STRPL,
	STRVS,
	STRVC,
	STRHI,
	STRLS,
	STRGE,
	STRLT,
	STRGT,
	STRLE,
	STRAL,
	
	STRB,
	STREQB,
	STRNEB,
	STRCSB,
	STRCCB,
	STRMIB,
	STRPLB,
	STRVSB,
	STRVCB,
	STRHIB,
	STRLSB,
	STRGEB,
	STRLTB,
	STRGTB,
	STRLEB,
	STRALB,
	
	STRT,
	STREQT,
	STRNET,
	STRCST,
	STRCCT,
	STRMIT,
	STRPLT,
	STRVST,
	STRVCT,
	STRHIT,
	STRLST,
	STRGET,
	STRLTT,
	STRGTT,
	STRLET,
	STRALT,
	
	STRBT,
	STREQBT,
	STRNEBT,
	STRCSBT,
	STRCCBT,
	STRMIBT,
	STRPLBT,
	STRVSBT,
	STRVCBT,
	STRHIBT,
	STRLSBT,
	STRGEBT,
	STRLTBT,
	STRGTBT,
	STRLEBT,
	STRALBT,
	
	//LDRH
	LDRH,
	LDREQH,
	LDRNEH,
	LDRCSH,
	LDRCCH,
	LDRMIH,
	LDRPLH,
	LDRVSH,
	LDRVCH,
	LDRHIH,
	LDRLSH,
	LDRGEH,
	LDRLTH,
	LDRGTH,
	LDRLEH,
	LDRALH,
	
	//LDRSH
	LDRSH,
	LDREQSH,
	LDRNESH,
	LDRCSSH,
	LDRCCSH,
	LDRMISH,
	LDRPLSH,
	LDRVSSH,
	LDRVCSH,
	LDRHISH,
	LDRLSSH,
	LDRGESH,
	LDRLTSH,
	LDRGTSH,
	LDRLESH,
	LDRALSH,
	
	//LDRSB
	LDRSB,
	LDREQSB,
	LDRNESB,
	LDRCSSB,
	LDRCCSB,
	LDRMISB,
	LDRPLSB,
	LDRVSSB,
	LDRVCSB,
	LDRHISB,
	LDRLSSB,
	LDRGESB,
	LDRLTSB,
	LDRGTSB,
	LDRLESB,
	LDRALSB,
	
	//STRH
	STRH,
	STREQH,
	STRNEH,
	STRCSH,
	STRCCH,
	STRMIH,
	STRPLH,
	STRVSH,
	STRVCH,
	STRHIH,
	STRLSH,
	STRGEH,
	STRLTH,
	STRGTH,
	STRLEH,
	STRALH,
	
	//STRSH
	STRSH,
	STREQSH,
	STRNESH,
	STRCSSH,
	STRCCSH,
	STRMISH,
	STRPLSH,
	STRVSSH,
	STRVCSH,
	STRHISH,
	STRLSSH,
	STRGESH,
	STRLTSH,
	STRGTSH,
	STRLESH,
	STRALSH,
	
	//STRSB
	STRSB,
	STREQSB,
	STRNESB,
	STRCSSB,
	STRCCSB,
	STRMISB,
	STRPLSB,
	STRVSSB,
	STRVCSB,
	STRHISB,
	STRLSSB,
	STRGESB,
	STRLTSB,
	STRGTSB,
	STRLESB,
	STRALSB,
	
	//LDM
	LDMFD,
	LDMEQFD,
	LDMNEFD,
	LDMCSFD,
	LDMCCFD,
	LDMMIFD,
	LDMPLFD,
	LDMVSFD,
	LDMVCFD,
	LDMHIFD,
	LDMLSFD,
	LDMGEFD,
	LDMLTFD,
	LDMGTFD,
	LDMLEFD,
	LDMALFD,
	
	LDMED,
	LDMEQED,
	LDMNEED,
	LDMCSED,
	LDMCCED,
	LDMMIED,
	LDMPLED,
	LDMVSED,
	LDMVCED,
	LDMHIED,
	LDMLSED,
	LDMGEED,
	LDMLTED,
	LDMGTED,
	LDMLEED,
	LDMALED,
	
	LDMFA,
	LDMEQFA,
	LDMNEFA,
	LDMCSFA,
	LDMCCFA,
	LDMMIFA,
	LDMPLFA,
	LDMVSFA,
	LDMVCFA,
	LDMHIFA,
	LDMLSFA,
	LDMGEFA,
	LDMLTFA,
	LDMGTFA,
	LDMLEFA,
	LDMALFA,
	
	LDMEA,
	LDMEQEA,
	LDMNEEA,
	LDMCSEA,
	LDMCCEA,
	LDMMIEA,
	LDMPLEA,
	LDMVSEA,
	LDMVCEA,
	LDMHIEA,
	LDMLSEA,
	LDMGEEA,
	LDMLTEA,
	LDMGTEA,
	LDMLEEA,
	LDMALEA,
	
	LDMIA,
	LDMEQIA,
	LDMNEIA,
	LDMCSIA,
	LDMCCIA,
	LDMMIIA,
	LDMPLIA,
	LDMVSIA,
	LDMVCIA,
	LDMHIIA,
	LDMLSIA,
	LDMGEIA,
	LDMLTIA,
	LDMGTIA,
	LDMLEIA,
	LDMALIA,
	
	LDMIB,
	LDMEQIB,
	LDMNEIB,
	LDMCSIB,
	LDMCCIB,
	LDMMIIB,
	LDMPLIB,
	LDMVSIB,
	LDMVCIB,
	LDMHIIB,
	LDMLSIB,
	LDMGEIB,
	LDMLTIB,
	LDMGTIB,
	LDMLEIB,
	LDMALIB,
	
	LDMDA,
	LDMEQDA,
	LDMNEDA,
	LDMCSDA,
	LDMCCDA,
	LDMMIDA,
	LDMPLDA,
	LDMVSDA,
	LDMVCDA,
	LDMHIDA,
	LDMLSDA,
	LDMGEDA,
	LDMLTDA,
	LDMGTDA,
	LDMLEDA,
	LDMALDA,
	
	LDMDB,
	LDMEQDB,
	LDMNEDB,
	LDMCSDB,
	LDMCCDB,
	LDMMIDB,
	LDMPLDB,
	LDMVSDB,
	LDMVCDB,
	LDMHIDB,
	LDMLSDB,
	LDMGEDB,
	LDMLTDB,
	LDMGTDB,
	LDMLEDB,
	LDMALDB,
	
	//STM
	STMFD,
	STMEQFD,
	STMNEFD,
	STMCSFD,
	STMCCFD,
	STMMIFD,
	STMPLFD,
	STMVSFD,
	STMVCFD,
	STMHIFD,
	STMLSFD,
	STMGEFD,
	STMLTFD,
	STMGTFD,
	STMLEFD,
	STMALFD,
	
	STMED,
	STMEQED,
	STMNEED,
	STMCSED,
	STMCCED,
	STMMIED,
	STMPLED,
	STMVSED,
	STMVCED,
	STMHIED,
	STMLSED,
	STMGEED,
	STMLTED,
	STMGTED,
	STMLEED,
    STMALED,
	
	STMFA,
	STMEQFA,
	STMNEFA,
	STMCSFA,
	STMCCFA,
	STMMIFA,
	STMPLFA,
	STMVSFA,
	STMVCFA,
	STMHIFA,
	STMLSFA,
	STMGEFA,
	STMLTFA,
	STMGTFA,
	STMLEFA,
	STMALFA,
	
	STMEA,
	STMEQEA,
	STMNEEA,
	STMCSEA,
	STMCCEA,
	STMMIEA,
	STMPLEA,
	STMVSEA,
	STMVCEA,
	STMHIEA,
	STMLSEA,
	STMGEEA,
	STMLTEA,
	STMGTEA,
	STMLEEA,
	STMALEA,
	
	STMIA,
	STMEQIA,
	STMNEIA,
	STMCSIA,
	STMCCIA,
	STMMIIA,
	STMPLIA,
	STMVSIA,
	STMVCIA,
	STMHIIA,
	STMLSIA,
	STMGEIA,
	STMLTIA,
	STMGTIA,
	STMLEIA,
	STMALIA,
	
	STMIB,
	STMEQIB,
	STMNEIB,
	STMCSIB,
	STMCCIB,
	STMMIIB,
	STMPLIB,
	STMVSIB,
	STMVCIB,
	STMHIIB,
	STMLSIB,
	STMGEIB,
	STMLTIB,
	STMGTIB,
	STMLEIB,
	STMALIB,
	
	STMDA,
	STMEQDA,
	STMNEDA,
	STMCSDA,
	STMCCDA,
	STMMIDA,
	STMPLDA,
	STMVSDA,
	STMVCDA,
	STMHIDA,
	STMLSDA,
	STMGEDA,
	STMLTDA,
	STMGTDA,
	STMLEDA,
	STMALDA,
	
	STMDB,
	STMEQDB,
	STMNEDB,
	STMCSDB,
	STMCCDB,
	STMMIDB,
	STMPLDB,
	STMVSDB,
	STMVCDB,
	STMHIDB,
	STMLSDB,
	STMGEDB,
	STMLTDB,
	STMGTDB,
	STMLEDB,
	STMALDB,
	
	//STW
	STW,
	STWEQ,
	STWNE,
	STWCS,
	STWCC,
	STWMI,
	STWPL,
	STWVS,
	STWVC,
	STWHI,
	STWLS,
	STWGE,
	STWLT,
	STWGT,
	STWLE,
	STWAL,
	
	STWB,
	STWEQB,
	STWNEB,
	STWCSB,
	STWCCB,
	STWMIB,
	STWPLB,
	STWVSB,
	STWVCB,
	STWHIB,
	STWLSB,
	STWGEB,
	STWLTB,
	STWGTB,
	STWLEB,
	STWALB,
	
	//SWI
	SWI,
	SWIEQ,
	SWINE,
	SWICS,
	SWICC,
	SWIMI,
	SWIPL,
	SWIVS,
	SWIVC,
	SWIHI,
	SWILS,
	SWIGE,
	SWILT,
	SWIGT,
	SWILE,
	SWIAL,
	
	//STOP
	STOP,
	
	//Operators
	COMMA,
	EXPL,
	RBRACK,
	LBRACK,
	LCURL,
	RCURL,
	CARROT,
	MINUS
    }
    
    private static HashMap <String, Token.Type> ops;
    private static HashMap <String, Token.Type> instructions;
    private static HashMap <String, Token.Type> registers;
    
    static {
    	//ops
    	ops = new HashMap<>();
    	ops.put(",", Token.Type.COMMA);
    	ops.put("!", Token.Type.EXPL);
    	ops.put("[", Token.Type.LBRACK);
    	ops.put("]", Token.Type.RBRACK);
    	ops.put("{", Token.Type.LCURL);
    	ops.put("}", Token.Type.RCURL);
    	ops.put("^", Token.Type.CARROT);
    	ops.put("-", Token.Type.MINUS);
    	
    	registers = new HashMap<>();
    	registers.put("R0", Token.Type.R0);
    	registers.put("R1", Token.Type.R1);
    	registers.put("R2", Token.Type.R2);
    	registers.put("R3", Token.Type.R3);
    	registers.put("R4", Token.Type.R4);
    	registers.put("R5", Token.Type.R5);
    	registers.put("R6", Token.Type.R6);
    	registers.put("R7", Token.Type.R7);
    	registers.put("R8", Token.Type.R8);
    	registers.put("R9", Token.Type.R9);
    	registers.put("R10", Token.Type.R10);
    	registers.put("R11", Token.Type.R11);
    	registers.put("R12", Token.Type.R12);
    	registers.put("R13", Token.Type.R13);
    	registers.put("R14", Token.Type.R14);
    	registers.put("R15", Token.Type.R15);
    	registers.put("CPSR", Token.Type.CPSR);
    	
    	//instructions
    	instructions = new HashMap<>();
    	
    	//BX instructions
    	instructions.put("BX", Token.Type.BX);
    	instructions.put("BXEQ", Token.Type.BXEQ);
    	instructions.put("BXNE", Token.Type.BXNE);
    	instructions.put("BXCS", Token.Type.BXCS);
    	instructions.put("BXCC", Token.Type.BXCC);
    	instructions.put("BXMI", Token.Type.BXMI);
    	instructions.put("BXPL", Token.Type.BXPL);
    	instructions.put("BXVS", Token.Type.BXVS);
    	instructions.put("BXVC", Token.Type.BXVC);
    	instructions.put("BXHI", Token.Type.BXHI);
    	instructions.put("BXLE", Token.Type.BXLE);
    	instructions.put("BXAL", Token.Type.BXAL);
    	
    	//B - BL
    	instructions.put("B", Token.Type.B);
    	instructions.put("BEQ", Token.Type.BEQ);
    	instructions.put("BNE", Token.Type.BNE);
    	instructions.put("BCS", Token.Type.BCS);
    	instructions.put("BCC", Token.Type.BCC);
    	instructions.put("BMI", Token.Type.BMI);
    	instructions.put("BPL", Token.Type.BPL);
    	instructions.put("BVS", Token.Type.BVS);
    	instructions.put("BVC", Token.Type.BVC);
    	instructions.put("BHI", Token.Type.BHI);
    	instructions.put("BLE", Token.Type.BLE);
    	instructions.put("BAL", Token.Type.BAL);
    	
    	instructions.put("BL", Token.Type.BL);
    	instructions.put("BLEQ", Token.Type.BLEQ);
    	instructions.put("BLNE", Token.Type.BLNE);
    	instructions.put("BLCS", Token.Type.BLCS);
    	instructions.put("BLCC", Token.Type.BLCC);
    	instructions.put("BLMI", Token.Type.BLMI);
    	instructions.put("BLPL", Token.Type.BLPL);
    	instructions.put("BLVS", Token.Type.BLVS);
    	instructions.put("BLVC", Token.Type.BLVC);
    	instructions.put("BLHI", Token.Type.BLHI);
    	instructions.put("BLLE", Token.Type.BLLE);
    	instructions.put("BLAL", Token.Type.BLAL);
    	
    	//MOV
    	
    	instructions.put("MOV", Token.Type.MOV);
    	instructions.put("MOVEQ", Token.Type.MOVEQ);
    	instructions.put("MOVNE", Token.Type.MOVNE);
    	instructions.put("MOVCS", Token.Type.MOVCS);
    	instructions.put("MOVCC", Token.Type.MOVCC);
    	instructions.put("MOVMI", Token.Type.MOVMI);
    	instructions.put("MOVPL", Token.Type.MOVPL);
    	instructions.put("MOVVS", Token.Type.MOVVS);
    	instructions.put("MOVVC", Token.Type.MOVVC);
    	instructions.put("MOVHI", Token.Type.MOVHI);
    	instructions.put("MOVLS", Token.Type.MOVLS);
    	instructions.put("MOVGE", Token.Type.MOVGE);
    	instructions.put("MOVLT", Token.Type.MOVLT);
    	instructions.put("MOVGT", Token.Type.MOVGT);
    	instructions.put("MOVLE", Token.Type.MOVLE);
    	instructions.put("MOVAL", Token.Type.MOVAL);
    	
    	instructions.put("MOVS", Token.Type.MOVS);
    	instructions.put("MOVEQS", Token.Type.MOVEQS);
    	instructions.put("MOVNES", Token.Type.MOVNES);
    	instructions.put("MOVCSS", Token.Type.MOVCSS);
    	instructions.put("MOVCCS", Token.Type.MOVCCS);
    	instructions.put("MOVMIS", Token.Type.MOVMIS);
    	instructions.put("MOVPLS", Token.Type.MOVPLS);
    	instructions.put("MOVVSS", Token.Type.MOVVSS);
    	instructions.put("MOVVCS", Token.Type.MOVVCS);
    	instructions.put("MOVHIS", Token.Type.MOVHIS);
    	instructions.put("MOVLSS", Token.Type.MOVLSS);
    	instructions.put("MOVGES", Token.Type.MOVGES);
    	instructions.put("MOVLTS", Token.Type.MOVLTS);
    	instructions.put("MOVGTS", Token.Type.MOVGTS);
    	instructions.put("MOVLES", Token.Type.MOVLES);
    	instructions.put("MOVALS", Token.Type.MOVALS);
    	
    	//MVN
    	
    	instructions.put("MVN", Token.Type.MVN);
    	instructions.put("MVNEQ", Token.Type.MVNEQ);
    	instructions.put("MVNNE", Token.Type.MVNNE);
    	instructions.put("MVNCS", Token.Type.MVNCS);
    	instructions.put("MVNCC", Token.Type.MVNCC);
    	instructions.put("MVNMI", Token.Type.MVNMI);
    	instructions.put("MVNPL", Token.Type.MVNPL);
    	instructions.put("MVNVS", Token.Type.MVNVS);
    	instructions.put("MVNVC", Token.Type.MVNVC);
    	instructions.put("MVNHI", Token.Type.MVNHI);
    	instructions.put("MVNLS", Token.Type.MVNLS);
    	instructions.put("MVNGE", Token.Type.MVNGE);
    	instructions.put("MVNLT", Token.Type.MVNLT);
    	instructions.put("MVNGT", Token.Type.MVNGT);
    	instructions.put("MVNLE", Token.Type.MVNLE);
    	instructions.put("MVNAL", Token.Type.MVNAL);
    	
    	instructions.put("MVNS", Token.Type.MVNS);
    	instructions.put("MVNEQS", Token.Type.MVNEQS);
    	instructions.put("MVNNES", Token.Type.MVNNES);
    	instructions.put("MVNCSS", Token.Type.MVNCSS);
    	instructions.put("MVNCCS", Token.Type.MVNCCS);
    	instructions.put("MVNMIS", Token.Type.MVNMIS);
    	instructions.put("MVNPLS", Token.Type.MVNPLS);
    	instructions.put("MVNVSS", Token.Type.MVNVSS);
    	instructions.put("MVNVCS", Token.Type.MVNVCS);
    	instructions.put("MVNHIS", Token.Type.MVNHIS);
    	instructions.put("MVNLSS", Token.Type.MVNLSS);
    	instructions.put("MVNGES", Token.Type.MVNGES);
    	instructions.put("MVNLTS", Token.Type.MVNLTS);
    	instructions.put("MVNGTS", Token.Type.MVNGTS);
    	instructions.put("MVNLES", Token.Type.MVNLES);
    	instructions.put("MVNALS", Token.Type.MVNALS);
    	
    	//CMP
    	instructions.put("CMP", Token.Type.CMP);
    	instructions.put("CMPEQ", Token.Type.CMPEQ);
    	instructions.put("CMPNE", Token.Type.CMPNE);
    	instructions.put("CMPCS", Token.Type.CMPCS);
    	instructions.put("CMPCC", Token.Type.CMPCC);
    	instructions.put("CMPMI", Token.Type.CMPMI);
    	instructions.put("CMPPL", Token.Type.CMPPL);
    	instructions.put("CMPVS", Token.Type.CMPVS);
    	instructions.put("CMPVC", Token.Type.CMPVC);
    	instructions.put("CMPHI", Token.Type.CMPHI);
    	instructions.put("CMPLS", Token.Type.CMPLS);
    	instructions.put("CMPGE", Token.Type.CMPGE);
    	instructions.put("CMPLT", Token.Type.CMPLT);
    	instructions.put("CMPGT", Token.Type.CMPGT);
    	instructions.put("CMPLE", Token.Type.CMPLE);
    	instructions.put("CMPAL", Token.Type.CMPAL);
    	
    	//CMN
    	instructions.put("CMN", Token.Type.CMN);
    	instructions.put("CMNEQ", Token.Type.CMNEQ);
    	instructions.put("CMNNE", Token.Type.CMNNE);
    	instructions.put("CMNCS", Token.Type.CMNCS);
    	instructions.put("CMNCC", Token.Type.CMNCC);
    	instructions.put("CMNMI", Token.Type.CMNMI);
    	instructions.put("CMNPL", Token.Type.CMNPL);
    	instructions.put("CMNVS", Token.Type.CMNVS);
    	instructions.put("CMNVC", Token.Type.CMNVC);
    	instructions.put("CMNHI", Token.Type.CMNHI);
    	instructions.put("CMNLS", Token.Type.CMNLS);
    	instructions.put("CMNGE", Token.Type.CMNGE);
    	instructions.put("CMNLT", Token.Type.CMNLT);
    	instructions.put("CMNGT", Token.Type.CMNGT);
    	instructions.put("CMNLE", Token.Type.CMNLE);
    	instructions.put("CMNAL", Token.Type.CMNAL);
    	
    	//TEQ
    	instructions.put("TEQ", Token.Type.TEQ);
    	instructions.put("TEQEQ", Token.Type.TEQEQ);
    	instructions.put("TEQNE", Token.Type.TEQNE);
    	instructions.put("TEQCS", Token.Type.TEQCS);
    	instructions.put("TEQCC", Token.Type.TEQCC);
    	instructions.put("TEQMI", Token.Type.TEQMI);
    	instructions.put("TEQPL", Token.Type.TEQPL);
    	instructions.put("TEQVS", Token.Type.TEQVS);
    	instructions.put("TEQVC", Token.Type.TEQVC);
    	instructions.put("TEQHI", Token.Type.TEQHI);
    	instructions.put("TEQLS", Token.Type.TEQLS);
    	instructions.put("TEQGE", Token.Type.TEQGE);
    	instructions.put("TEQLT", Token.Type.TEQLT);
    	instructions.put("TEQGT", Token.Type.TEQGT);
    	instructions.put("TEQLE", Token.Type.TEQLE);
    	instructions.put("TEQAL", Token.Type.TEQAL);
    	
    	//TEQ
    	instructions.put("TST", Token.Type.TST);
    	instructions.put("TSTEQ", Token.Type.TSTEQ);
    	instructions.put("TSTNE", Token.Type.TSTNE);
    	instructions.put("TSTCS", Token.Type.TSTCS);
    	instructions.put("TSTCC", Token.Type.TSTCC);
    	instructions.put("TSTMI", Token.Type.TSTMI);
    	instructions.put("TSTPL", Token.Type.TSTPL);
    	instructions.put("TSTVS", Token.Type.TSTVS);
    	instructions.put("TSTVC", Token.Type.TSTVC);
    	instructions.put("TSTHI", Token.Type.TSTHI);
    	instructions.put("TSTLS", Token.Type.TSTLS);
    	instructions.put("TSTGE", Token.Type.TSTGE);
    	instructions.put("TSTLT", Token.Type.TSTLT);
    	instructions.put("TSTGT", Token.Type.TSTGT);
    	instructions.put("TSTLE", Token.Type.TSTLE);
    	instructions.put("TSTAL", Token.Type.TSTAL);
    	
    	//AND
    	instructions.put("AND", Token.Type.AND);
    	instructions.put("ANDEQ", Token.Type.ANDEQ);
    	instructions.put("ANDNE", Token.Type.ANDNE);
    	instructions.put("ANDCS", Token.Type.ANDCS);
    	instructions.put("ANDCC", Token.Type.ANDCC);
    	instructions.put("ANDMI", Token.Type.ANDMI);
    	instructions.put("ANDPL", Token.Type.ANDPL);
    	instructions.put("ANDVS", Token.Type.ANDVS);
    	instructions.put("ANDVC", Token.Type.ANDVC);
    	instructions.put("ANDHI", Token.Type.ANDHI);
    	instructions.put("ANDLS", Token.Type.ANDLS);
    	instructions.put("ANDGE", Token.Type.ANDGE);
    	instructions.put("ANDLT", Token.Type.ANDLT);
    	instructions.put("ANDGT", Token.Type.ANDGT);
    	instructions.put("ANDLE", Token.Type.ANDLE);
    	instructions.put("ANDAL", Token.Type.ANDAL);
    	
    	instructions.put("ANDS", Token.Type.ANDS);
    	instructions.put("ANDEQS", Token.Type.ANDEQS);
    	instructions.put("ANDNES", Token.Type.ANDNES);
    	instructions.put("ANDCSS", Token.Type.ANDCSS);
    	instructions.put("ANDCCS", Token.Type.ANDCCS);
    	instructions.put("ANDMIS", Token.Type.ANDMIS);
    	instructions.put("ANDPLS", Token.Type.ANDPLS);
    	instructions.put("ANDVSS", Token.Type.ANDVSS);
    	instructions.put("ANDVCS", Token.Type.ANDVCS);
    	instructions.put("ANDHIS", Token.Type.ANDHIS);
    	instructions.put("ANDLSS", Token.Type.ANDLSS);
    	instructions.put("ANDGES", Token.Type.ANDGES);
    	instructions.put("ANDLTS", Token.Type.ANDLTS);
    	instructions.put("ANDGTS", Token.Type.ANDGTS);
    	instructions.put("ANDLES", Token.Type.ANDLES);
    	instructions.put("ANDALS", Token.Type.ANDALS);
    	
    	//EOR
    	instructions.put("EOR", Token.Type.EOR);
    	instructions.put("EOREQ", Token.Type.EOREQ);
    	instructions.put("EORNE", Token.Type.EORNE);
    	instructions.put("EORCS", Token.Type.EORCS);
    	instructions.put("EORCC", Token.Type.EORCC);
    	instructions.put("EORMI", Token.Type.EORMI);
    	instructions.put("EORPL", Token.Type.EORPL);
    	instructions.put("EORVS", Token.Type.EORVS);
    	instructions.put("EORVC", Token.Type.EORVC);
    	instructions.put("EORHI", Token.Type.EORHI);
    	instructions.put("EORLS", Token.Type.EORLS);
    	instructions.put("EORGE", Token.Type.EORGE);
    	instructions.put("EORLT", Token.Type.EORLT);
    	instructions.put("EORGT", Token.Type.EORGT);
    	instructions.put("EORLE", Token.Type.EORLE);
    	instructions.put("EORAL", Token.Type.EORAL);
    	
    	instructions.put("EORS", Token.Type.EORS);
    	instructions.put("EOREQS", Token.Type.EOREQS);
    	instructions.put("EORNES", Token.Type.EORNES);
    	instructions.put("EORCSS", Token.Type.EORCSS);
    	instructions.put("EORCCS", Token.Type.EORCCS);
    	instructions.put("EORMIS", Token.Type.EORMIS);
    	instructions.put("EORPLS", Token.Type.EORPLS);
    	instructions.put("EORVSS", Token.Type.EORVSS);
    	instructions.put("EORVCS", Token.Type.EORVCS);
    	instructions.put("EORHIS", Token.Type.EORHIS);
    	instructions.put("EORLSS", Token.Type.EORLSS);
    	instructions.put("EORGES", Token.Type.EORGES);
    	instructions.put("EORLTS", Token.Type.EORLTS);
    	instructions.put("EORGTS", Token.Type.EORGTS);
    	instructions.put("EORLES", Token.Type.EORLES);
    	instructions.put("EORALS", Token.Type.EORALS);
    	
    	//SUB
    	instructions.put("SUB", Token.Type.SUB);
    	instructions.put("SUBEQ", Token.Type.SUBEQ);
    	instructions.put("SUBNE", Token.Type.SUBNE);
    	instructions.put("SUBCS", Token.Type.SUBCS);
    	instructions.put("SUBCC", Token.Type.SUBCC);
    	instructions.put("SUBMI", Token.Type.SUBMI);
    	instructions.put("SUBPL", Token.Type.SUBPL);
    	instructions.put("SUBVS", Token.Type.SUBVS);
    	instructions.put("SUBVC", Token.Type.SUBVC);
    	instructions.put("SUBHI", Token.Type.SUBHI);
    	instructions.put("SUBLS", Token.Type.SUBLS);
    	instructions.put("SUBGE", Token.Type.SUBGE);
    	instructions.put("SUBLT", Token.Type.SUBLT);
    	instructions.put("SUBGT", Token.Type.SUBGT);
    	instructions.put("SUBLE", Token.Type.SUBLE);
    	instructions.put("SUBAL", Token.Type.SUBAL);
    	
    	instructions.put("SUBS", Token.Type.SUBS);
    	instructions.put("SUBEQS", Token.Type.SUBEQS);
    	instructions.put("SUBNES", Token.Type.SUBNES);
    	instructions.put("SUBCSS", Token.Type.SUBCSS);
    	instructions.put("SUBCCS", Token.Type.SUBCCS);
    	instructions.put("SUBMIS", Token.Type.SUBMIS);
    	instructions.put("SUBPLS", Token.Type.SUBPLS);
    	instructions.put("SUBVSS", Token.Type.SUBVSS);
    	instructions.put("SUBVCS", Token.Type.SUBVCS);
    	instructions.put("SUBHIS", Token.Type.SUBHIS);
    	instructions.put("SUBLSS", Token.Type.SUBLSS);
    	instructions.put("SUBGES", Token.Type.SUBGES);
    	instructions.put("SUBLTS", Token.Type.SUBLTS);
    	instructions.put("SUBGTS", Token.Type.SUBGTS);
    	instructions.put("SUBLES", Token.Type.SUBLES);
    	instructions.put("SUBALS", Token.Type.SUBALS);
    	
    	//RSB
    	instructions.put("RSB", Token.Type.RSB);
    	instructions.put("RSBEQ", Token.Type.RSBEQ);
    	instructions.put("RSBNE", Token.Type.RSBNE);
    	instructions.put("RSBCS", Token.Type.RSBCS);
    	instructions.put("RSBCC", Token.Type.RSBCC);
    	instructions.put("RSBMI", Token.Type.RSBMI);
    	instructions.put("RSBPL", Token.Type.RSBPL);
    	instructions.put("RSBVS", Token.Type.RSBVS);
    	instructions.put("RSBVC", Token.Type.RSBVC);
    	instructions.put("RSBHI", Token.Type.RSBHI);
    	instructions.put("RSBLS", Token.Type.RSBLS);
    	instructions.put("RSBGE", Token.Type.RSBGE);
    	instructions.put("RSBLT", Token.Type.RSBLT);
    	instructions.put("RSBGT", Token.Type.RSBGT);
    	instructions.put("RSBLE", Token.Type.RSBLE);
    	instructions.put("RSBAL", Token.Type.RSBAL);
    	
    	instructions.put("RSBS", Token.Type.RSBS);
    	instructions.put("RSBEQS", Token.Type.RSBEQS);
    	instructions.put("RSBNES", Token.Type.RSBNES);
    	instructions.put("RSBCSS", Token.Type.RSBCSS);
    	instructions.put("RSBCCS", Token.Type.RSBCCS);
    	instructions.put("RSBMIS", Token.Type.RSBMIS);
    	instructions.put("RSBPLS", Token.Type.RSBPLS);
    	instructions.put("RSBVSS", Token.Type.RSBVSS);
    	instructions.put("RSBVCS", Token.Type.RSBVCS);
    	instructions.put("RSBHIS", Token.Type.RSBHIS);
    	instructions.put("RSBLSS", Token.Type.RSBLSS);
    	instructions.put("RSBGES", Token.Type.RSBGES);
    	instructions.put("RSBLTS", Token.Type.RSBLTS);
    	instructions.put("RSBGTS", Token.Type.RSBGTS);
    	instructions.put("RSBLES", Token.Type.RSBLES);
    	instructions.put("RSBALS", Token.Type.RSBALS);
    	
    	//ADD
    	instructions.put("ADD", Token.Type.ADD);
    	instructions.put("ADDEQ", Token.Type.ADDEQ);
    	instructions.put("ADDNE", Token.Type.ADDNE);
    	instructions.put("ADDCS", Token.Type.ADDCS);
    	instructions.put("ADDCC", Token.Type.ADDCC);
    	instructions.put("ADDMI", Token.Type.ADDMI);
    	instructions.put("ADDPL", Token.Type.ADDPL);
    	instructions.put("ADDVS", Token.Type.ADDVS);
    	instructions.put("ADDVC", Token.Type.ADDVC);
    	instructions.put("ADDHI", Token.Type.ADDHI);
    	instructions.put("ADDLS", Token.Type.ADDLS);
    	instructions.put("ADDGE", Token.Type.ADDGE);
    	instructions.put("ADDLT", Token.Type.ADDLT);
    	instructions.put("ADDGT", Token.Type.ADDGT);
    	instructions.put("ADDLE", Token.Type.ADDLE);
    	instructions.put("ADDAL", Token.Type.ADDAL);
   
    	instructions.put("ADDS", Token.Type.ADDS);
    	instructions.put("ADDEQS", Token.Type.ADDEQS);
    	instructions.put("ADDNES", Token.Type.ADDNES);
    	instructions.put("ADDCSS", Token.Type.ADDCSS);
    	instructions.put("ADDCCS", Token.Type.ADDCCS);
    	instructions.put("ADDMIS", Token.Type.ADDMIS);
    	instructions.put("ADDPLS", Token.Type.ADDPLS);
    	instructions.put("ADDVSS", Token.Type.ADDVSS);
    	instructions.put("ADDVCS", Token.Type.ADDVCS);
    	instructions.put("ADDHIS", Token.Type.ADDHIS);
    	instructions.put("ADDLSS", Token.Type.ADDLSS);
    	instructions.put("ADDGES", Token.Type.ADDGES);
    	instructions.put("ADDLTS", Token.Type.ADDLTS);
    	instructions.put("ADDGTS", Token.Type.ADDGTS);
    	instructions.put("ADDLES", Token.Type.ADDLES);
    	instructions.put("ADDALS", Token.Type.ADDALS);
    	
    	//ADC
    	instructions.put("ADC", Token.Type.ADC);
    	instructions.put("ADCEQ", Token.Type.ADCEQ);
    	instructions.put("ADCNE", Token.Type.ADCNE);
    	instructions.put("ADCCS", Token.Type.ADCCS);
    	instructions.put("ADCCC", Token.Type.ADCCC);
    	instructions.put("ADCMI", Token.Type.ADCMI);
    	instructions.put("ADCPL", Token.Type.ADCPL);
    	instructions.put("ADCVS", Token.Type.ADCVS);
    	instructions.put("ADCVC", Token.Type.ADCVC);
    	instructions.put("ADCHI", Token.Type.ADCHI);
    	instructions.put("ADCLS", Token.Type.ADCLS);
    	instructions.put("ADCGE", Token.Type.ADCGE);
    	instructions.put("ADCLT", Token.Type.ADCLT);
    	instructions.put("ADCGT", Token.Type.ADCGT);
    	instructions.put("ADCLE", Token.Type.ADCLE);
    	instructions.put("ADCAL", Token.Type.ADCAL);
   
    	instructions.put("ADCS", Token.Type.ADCS);
    	instructions.put("ADCEQS", Token.Type.ADCEQS);
    	instructions.put("ADCNES", Token.Type.ADCNES);
    	instructions.put("ADCCSS", Token.Type.ADCCSS);
    	instructions.put("ADCCCS", Token.Type.ADCCCS);
    	instructions.put("ADCMIS", Token.Type.ADCMIS);
    	instructions.put("ADCPLS", Token.Type.ADCPLS);
    	instructions.put("ADCVSS", Token.Type.ADCVSS);
    	instructions.put("ADCVCS", Token.Type.ADCVCS);
    	instructions.put("ADCHIS", Token.Type.ADCHIS);
    	instructions.put("ADCLSS", Token.Type.ADCLSS);
    	instructions.put("ADCGES", Token.Type.ADCGES);
    	instructions.put("ADCLTS", Token.Type.ADCLTS);
    	instructions.put("ADCGTS", Token.Type.ADCGTS);
    	instructions.put("ADCLES", Token.Type.ADCLES);
    	instructions.put("ADCALS", Token.Type.ADCALS);
    	
    	//SBC
    	instructions.put("SBC", Token.Type.SBC);
    	instructions.put("SBCEQ", Token.Type.SBCEQ);
    	instructions.put("SBCNE", Token.Type.SBCNE);
    	instructions.put("SBCCS", Token.Type.SBCCS);
    	instructions.put("SBCCC", Token.Type.SBCCC);
    	instructions.put("SBCMI", Token.Type.SBCMI);
    	instructions.put("SBCPL", Token.Type.SBCPL);
    	instructions.put("SBCVS", Token.Type.SBCVS);
    	instructions.put("SBCVC", Token.Type.SBCVC);
    	instructions.put("SBCHI", Token.Type.SBCHI);
    	instructions.put("SBCLS", Token.Type.SBCLS);
    	instructions.put("SBCGE", Token.Type.SBCGE);
    	instructions.put("SBCLT", Token.Type.SBCLT);
    	instructions.put("SBCGT", Token.Type.SBCGT);
    	instructions.put("SBCLE", Token.Type.SBCLE);
    	instructions.put("SBCAL", Token.Type.SBCAL);
    	
    	instructions.put("SBCS", Token.Type.SBCS);
    	instructions.put("SBCEQS", Token.Type.SBCEQS);
    	instructions.put("SBCNES", Token.Type.SBCNES);
    	instructions.put("SBCCSS", Token.Type.SBCCSS);
    	instructions.put("SBCCCS", Token.Type.SBCCCS);
    	instructions.put("SBCMIS", Token.Type.SBCMIS);
    	instructions.put("SBCPLS", Token.Type.SBCPLS);
    	instructions.put("SBCVSS", Token.Type.SBCVSS);
    	instructions.put("SBCVCS", Token.Type.SBCVCS);
    	instructions.put("SBCHIS", Token.Type.SBCHIS);
    	instructions.put("SBCLSS", Token.Type.SBCLSS);
    	instructions.put("SBCGES", Token.Type.SBCGES);
    	instructions.put("SBCLTS", Token.Type.SBCLTS);
    	instructions.put("SBCGTS", Token.Type.SBCGTS);
    	instructions.put("SBCLES", Token.Type.SBCLES);
    	instructions.put("SBCALS", Token.Type.SBCALS);
    	
    	//RSB
    	instructions.put("RSC", Token.Type.RSC);
    	instructions.put("RSCEQ", Token.Type.RSCEQ);
    	instructions.put("RSCNE", Token.Type.RSCNE);
    	instructions.put("RSCCS", Token.Type.RSCCS);
    	instructions.put("RSCCC", Token.Type.RSCCC);
    	instructions.put("RSCMI", Token.Type.RSCMI);
    	instructions.put("RSCPL", Token.Type.RSCPL);
    	instructions.put("RSCVS", Token.Type.RSCVS);
    	instructions.put("RSCVC", Token.Type.RSCVC);
    	instructions.put("RSCHI", Token.Type.RSCHI);
    	instructions.put("RSCLS", Token.Type.RSCLS);
    	instructions.put("RSCGE", Token.Type.RSCGE);
    	instructions.put("RSCLT", Token.Type.RSCLT);
    	instructions.put("RSCGT", Token.Type.RSCGT);
    	instructions.put("RSCLE", Token.Type.RSCLE);
    	instructions.put("RSCAL", Token.Type.RSCAL);
    	
    	instructions.put("RSCS", Token.Type.RSCS);
    	instructions.put("RSCEQS", Token.Type.RSCEQS);
    	instructions.put("RSCNES", Token.Type.RSCNES);
    	instructions.put("RSCCSS", Token.Type.RSCCSS);
    	instructions.put("RSCCCS", Token.Type.RSCCCS);
    	instructions.put("RSCMIS", Token.Type.RSCMIS);
    	instructions.put("RSCPLS", Token.Type.RSCPLS);
    	instructions.put("RSCVSS", Token.Type.RSCVSS);
    	instructions.put("RSCVCS", Token.Type.RSCVCS);
    	instructions.put("RSCHIS", Token.Type.RSCHIS);
    	instructions.put("RSCLSS", Token.Type.RSCLSS);
    	instructions.put("RSCGES", Token.Type.RSCGES);
    	instructions.put("RSCLTS", Token.Type.RSCLTS);
    	instructions.put("RSCGTS", Token.Type.RSCGTS);
    	instructions.put("RSCLES", Token.Type.RSCLES);
    	instructions.put("RSCALS", Token.Type.RSCALS);
    	
    	//ORR
    	instructions.put("ORR", Token.Type.ORR);
    	instructions.put("ORREQ", Token.Type.ORREQ);
    	instructions.put("ORRNE", Token.Type.ORRNE);
    	instructions.put("ORRCS", Token.Type.ORRCS);
    	instructions.put("ORRCC", Token.Type.ORRCC);
    	instructions.put("ORRMI", Token.Type.ORRMI);
    	instructions.put("ORRPL", Token.Type.ORRPL);
    	instructions.put("ORRVS", Token.Type.ORRVS);
    	instructions.put("ORRVC", Token.Type.ORRVC);
    	instructions.put("ORRHI", Token.Type.ORRHI);
    	instructions.put("ORRLS", Token.Type.ORRLS);
    	instructions.put("ORRGE", Token.Type.ORRGE);
    	instructions.put("ORRLT", Token.Type.ORRLT);
    	instructions.put("ORRGT", Token.Type.ORRGT);
    	instructions.put("ORRLE", Token.Type.ORRLE);
    	instructions.put("ORRAL", Token.Type.ORRAL);
    	
    	instructions.put("ORRS", Token.Type.ORRS);
    	instructions.put("ORREQS", Token.Type.ORREQS);
    	instructions.put("ORRNES", Token.Type.ORRNES);
    	instructions.put("ORRCSS", Token.Type.ORRCSS);
    	instructions.put("ORRCCS", Token.Type.ORRCCS);
    	instructions.put("ORRMIS", Token.Type.ORRMIS);
    	instructions.put("ORRPLS", Token.Type.ORRPLS);
    	instructions.put("ORRVSS", Token.Type.ORRVSS);
    	instructions.put("ORRVCS", Token.Type.ORRVCS);
    	instructions.put("ORRHIS", Token.Type.ORRHIS);
    	instructions.put("ORRLSS", Token.Type.ORRLSS);
    	instructions.put("ORRGES", Token.Type.ORRGES);
    	instructions.put("ORRLTS", Token.Type.ORRLTS);
    	instructions.put("ORRGTS", Token.Type.ORRGTS);
    	instructions.put("ORRLES", Token.Type.ORRLES);
    	instructions.put("ORRALS", Token.Type.ORRALS);
    	
    	//BIC
    	instructions.put("BIC", Token.Type.BIC);
    	instructions.put("BICEQ", Token.Type.BICEQ);
    	instructions.put("BICNE", Token.Type.BICNE);
    	instructions.put("BICCS", Token.Type.BICCS);
    	instructions.put("BICCC", Token.Type.BICCC);
    	instructions.put("BICMI", Token.Type.BICMI);
    	instructions.put("BICPL", Token.Type.BICPL);
    	instructions.put("BICVS", Token.Type.BICVS);
    	instructions.put("BICVC", Token.Type.BICVC);
    	instructions.put("BICHI", Token.Type.BICHI);
    	instructions.put("BICLS", Token.Type.BICLS);
    	instructions.put("BICGE", Token.Type.BICGE);
    	instructions.put("BICLT", Token.Type.BICLT);
    	instructions.put("BICGT", Token.Type.BICGT);
    	instructions.put("BICLE", Token.Type.BICLE);
    	instructions.put("BICAL", Token.Type.BICAL);
    	
    	instructions.put("BICS", Token.Type.BICS);
    	instructions.put("BICEQS", Token.Type.BICEQS);
    	instructions.put("BICNES", Token.Type.BICNES);
    	instructions.put("BICCSS", Token.Type.BICCSS);
    	instructions.put("BICCCS", Token.Type.BICCCS);
    	instructions.put("BICMIS", Token.Type.BICMIS);
    	instructions.put("BICPLS", Token.Type.BICPLS);
    	instructions.put("BICVSS", Token.Type.BICVSS);
    	instructions.put("BICVCS", Token.Type.BICVCS);
    	instructions.put("BICHIS", Token.Type.BICHIS);
    	instructions.put("BICLSS", Token.Type.BICLSS);
    	instructions.put("BICGES", Token.Type.BICGES);
    	instructions.put("BICLTS", Token.Type.BICLTS);
    	instructions.put("BICGTS", Token.Type.BICGTS);
    	instructions.put("BICLES", Token.Type.BICLES);
    	instructions.put("BICALS", Token.Type.BICALS);
    	
    	//MRS
    	instructions.put("MRS", Token.Type.MRS);
    	instructions.put("MRSEQ", Token.Type.MRSEQ);
    	instructions.put("MRSNE", Token.Type.MRSNE);
    	instructions.put("MRSCS", Token.Type.MRSCS);
    	instructions.put("MRSCC", Token.Type.MRSCC);
    	instructions.put("MRSMI", Token.Type.MRSMI);
    	instructions.put("MRSPL", Token.Type.MRSPL);
    	instructions.put("MRSVS", Token.Type.MRSVS);
    	instructions.put("MRSVC", Token.Type.MRSVC);
    	instructions.put("MRSHI", Token.Type.MRSHI);
    	instructions.put("MRSLS", Token.Type.MRSLS);
    	instructions.put("MRSGE", Token.Type.MRSGE);
    	instructions.put("MRSLT", Token.Type.MRSLT);
    	instructions.put("MRSGT", Token.Type.MRSGT);
    	instructions.put("MRSLE", Token.Type.MRSLE);
    	instructions.put("MRSAL", Token.Type.MRSAL);
    	
    	//MSR
    	instructions.put("MSR", Token.Type.MSR);
    	instructions.put("MSREQ", Token.Type.MSREQ);
    	instructions.put("MSRNE", Token.Type.MSRNE);
    	instructions.put("MSRCS", Token.Type.MSRCS);
    	instructions.put("MSRCC", Token.Type.MSRCC);
    	instructions.put("MSRMI", Token.Type.MSRMI);
    	instructions.put("MSRPL", Token.Type.MSRPL);
    	instructions.put("MSRVS", Token.Type.MSRVS);
    	instructions.put("MSRVC", Token.Type.MSRVC);
    	instructions.put("MSRHI", Token.Type.MSRHI);
    	instructions.put("MSRLS", Token.Type.MSRLS);
    	instructions.put("MSRGE", Token.Type.MSRGE);
    	instructions.put("MSRLT", Token.Type.MSRLT);
    	instructions.put("MSRGT", Token.Type.MSRGT);
    	instructions.put("MSRLE", Token.Type.MSRLE);
    	instructions.put("MSRAL", Token.Type.MSRAL);
    	
    	//MUL
    	instructions.put("MUL", Token.Type.MUL);
    	instructions.put("MULEQ", Token.Type.MULEQ);
    	instructions.put("MULNE", Token.Type.MULNE);
    	instructions.put("MULCS", Token.Type.MULCS);
    	instructions.put("MULCC", Token.Type.MULCC);
    	instructions.put("MULMI", Token.Type.MULMI);
    	instructions.put("MULPL", Token.Type.MULPL);
    	instructions.put("MULVS", Token.Type.MULVS);
    	instructions.put("MULVC", Token.Type.MULVC);
    	instructions.put("MULHI", Token.Type.MULHI);
    	instructions.put("MULLS", Token.Type.MULLS);
    	instructions.put("MULGE", Token.Type.MULGE);
    	instructions.put("MULLT", Token.Type.MULLT);
    	instructions.put("MULGT", Token.Type.MULGT);
    	instructions.put("MULLE", Token.Type.MULLE);
    	instructions.put("MULAL", Token.Type.MULAL);
    	
    	instructions.put("MULS", Token.Type.MULS);
    	instructions.put("MULEQS", Token.Type.MULEQS);
    	instructions.put("MULNES", Token.Type.MULNES);
    	instructions.put("MULCSS", Token.Type.MULCSS);
    	instructions.put("MULCCS", Token.Type.MULCCS);
    	instructions.put("MULMIS", Token.Type.MULMIS);
    	instructions.put("MULPLS", Token.Type.MULPLS);
    	instructions.put("MULVSS", Token.Type.MULVSS);
    	instructions.put("MULVCS", Token.Type.MULVCS);
    	instructions.put("MULHIS", Token.Type.MULHIS);
    	instructions.put("MULLSS", Token.Type.MULLSS);
    	instructions.put("MULGES", Token.Type.MULGES);
    	instructions.put("MULLTS", Token.Type.MULLTS);
    	instructions.put("MULGTS", Token.Type.MULGTS);
    	instructions.put("MULLES", Token.Type.MULLES);
    	instructions.put("MULALS", Token.Type.MULALS);
    	
    	//MLA
    	instructions.put("MLA", Token.Type.MLA);
    	instructions.put("MLAEQ", Token.Type.MLAEQ);
    	instructions.put("MLANE", Token.Type.MLANE);
    	instructions.put("MLACS", Token.Type.MLACS);
    	instructions.put("MLACC", Token.Type.MLACC);
    	instructions.put("MLAMI", Token.Type.MLAMI);
    	instructions.put("MLAPL", Token.Type.MLAPL);
    	instructions.put("MLAVS", Token.Type.MLAVS);
    	instructions.put("MLAVC", Token.Type.MLAVC);
    	instructions.put("MLAHI", Token.Type.MLAHI);
    	instructions.put("MLALS", Token.Type.MLALS);
    	instructions.put("MLAGE", Token.Type.MLAGE);
    	instructions.put("MLALT", Token.Type.MLALT);
    	instructions.put("MLAGT", Token.Type.MLAGT);
    	instructions.put("MLALE", Token.Type.MLALE);
    	instructions.put("MLAAL", Token.Type.MLAAL);
    	
    	instructions.put("MLAS", Token.Type.MLA);
    	instructions.put("MLAEQS", Token.Type.MLAEQ);
    	instructions.put("MLANES", Token.Type.MLANE);
    	instructions.put("MLACSS", Token.Type.MLACS);
    	instructions.put("MLACCS", Token.Type.MLACC);
    	instructions.put("MLAMIS", Token.Type.MLAMI);
    	instructions.put("MLAPLS", Token.Type.MLAPL);
    	instructions.put("MLAVSS", Token.Type.MLAVS);
    	instructions.put("MLAVCS", Token.Type.MLAVC);
    	instructions.put("MLAHIS", Token.Type.MLAHI);
    	instructions.put("MLALSS", Token.Type.MLALS);
    	instructions.put("MLAGES", Token.Type.MLAGE);
    	instructions.put("MLALTS", Token.Type.MLALT);
    	instructions.put("MLAGTS", Token.Type.MLAGT);
    	instructions.put("MLALES", Token.Type.MLALE);
    	instructions.put("MLAALS", Token.Type.MLAAL);
    	
    	//UMULL
    	instructions.put("UMULL", Token.Type.UMULL);
    	instructions.put("UMULLEQ", Token.Type.UMULLEQ);
    	instructions.put("UMULLNE", Token.Type.UMULLNE);
    	instructions.put("UMULLCS", Token.Type.UMULLCS);
    	instructions.put("UMULLCC", Token.Type.UMULLCC);
    	instructions.put("UMULLMI", Token.Type.UMULLMI);
    	instructions.put("UMULLPL", Token.Type.UMULLPL);
    	instructions.put("UMULLVS", Token.Type.UMULLVS);
    	instructions.put("UMULLVC", Token.Type.UMULLVC);
    	instructions.put("UMULLHI", Token.Type.UMULLHI);
    	instructions.put("UMULLLS", Token.Type.UMULLLS);
    	instructions.put("UMULLGE", Token.Type.UMULLGE);
    	instructions.put("UMULLLT", Token.Type.UMULLLT);
    	instructions.put("UMULLGT", Token.Type.UMULLGT);
    	instructions.put("UMULLLE", Token.Type.UMULLLE);
    	instructions.put("UMULLAL", Token.Type.UMULLAL);
    	
    	instructions.put("UMULLS", Token.Type.UMULLS);
    	instructions.put("UMULLEQS", Token.Type.UMULLEQS);
    	instructions.put("UMULLNES", Token.Type.UMULLNES);
    	instructions.put("UMULLCSS", Token.Type.UMULLCSS);
    	instructions.put("UMULLCCS", Token.Type.UMULLCCS);
    	instructions.put("UMULLMIS", Token.Type.UMULLMIS);
    	instructions.put("UMULLPLS", Token.Type.UMULLPLS);
    	instructions.put("UMULLVSS", Token.Type.UMULLVSS);
    	instructions.put("UMULLVCS", Token.Type.UMULLVCS);
    	instructions.put("UMULLHIS", Token.Type.UMULLHIS);
    	instructions.put("UMULLLSS", Token.Type.UMULLLSS);
    	instructions.put("UMULLGES", Token.Type.UMULLGES);
    	instructions.put("UMULLLTS", Token.Type.UMULLLTS);
    	instructions.put("UMULLGTS", Token.Type.UMULLGTS);
    	instructions.put("UMULLLES", Token.Type.UMULLLES);
    	instructions.put("UMULLALS", Token.Type.UMULLALS);
    	
    	instructions.put("SMULL", Token.Type.SMULL);
    	instructions.put("SMULLEQ", Token.Type.SMULLEQ);
    	instructions.put("SMULLNE", Token.Type.SMULLNE);
    	instructions.put("SMULLCS", Token.Type.SMULLCS);
    	instructions.put("SMULLCC", Token.Type.SMULLCC);
    	instructions.put("SMULLMI", Token.Type.SMULLMI);
    	instructions.put("SMULLPL", Token.Type.SMULLPL);
    	instructions.put("SMULLVS", Token.Type.SMULLVS);
    	instructions.put("SMULLVC", Token.Type.SMULLVC);
    	instructions.put("SMULLHI", Token.Type.SMULLHI);
    	instructions.put("SMULLLS", Token.Type.SMULLLS);
    	instructions.put("SMULLGE", Token.Type.SMULLGE);
    	instructions.put("SMULLLT", Token.Type.SMULLLT);
    	instructions.put("SMULLGT", Token.Type.SMULLGT);
    	instructions.put("SMULLLE", Token.Type.SMULLLE);
    	instructions.put("SMULLAL", Token.Type.SMULLAL);
    
    	instructions.put("SMULLS", Token.Type.SMULLS);
    	instructions.put("SMULLEQS", Token.Type.SMULLEQS);
    	instructions.put("SMULLNES", Token.Type.SMULLNES);
    	instructions.put("SMULLCSS", Token.Type.SMULLCSS);
    	instructions.put("SMULLCCS", Token.Type.SMULLCCS);
    	instructions.put("SMULLMIS", Token.Type.SMULLMIS);
    	instructions.put("SMULLPLS", Token.Type.SMULLPLS);
    	instructions.put("SMULLVSS", Token.Type.SMULLVSS);
    	instructions.put("SMULLVCS", Token.Type.SMULLVCS);
    	instructions.put("SMULLHIS", Token.Type.SMULLHIS);
    	instructions.put("SMULLLSS", Token.Type.SMULLLSS);
    	instructions.put("SMULLGES", Token.Type.SMULLGES);
    	instructions.put("SMULLLTS", Token.Type.SMULLLTS);
    	instructions.put("SMULLGTS", Token.Type.SMULLGTS);
    	instructions.put("SMULLLES", Token.Type.SMULLLES);
    	instructions.put("SMULLALS", Token.Type.SMULLALS);
    	
    	//mlal
    	instructions.put("UMLAL", Token.Type.UMLAL);
    	instructions.put("UMLALEQ", Token.Type.UMLALEQ);
    	instructions.put("UMLALNE", Token.Type.UMLALNE);
    	instructions.put("UMLALCS", Token.Type.UMLALCS);
    	instructions.put("UMLALCC", Token.Type.UMLALCC);
    	instructions.put("UMLALMI", Token.Type.UMLALMI);
    	instructions.put("UMLALPL", Token.Type.UMLALPL);
    	instructions.put("UMLALVS", Token.Type.UMLALVS);
    	instructions.put("UMLALVC", Token.Type.UMLALVC);
    	instructions.put("UMLALHI", Token.Type.UMLALHI);
    	instructions.put("UMLALLS", Token.Type.UMLALLS);
    	instructions.put("UMLALGE", Token.Type.UMLALGE);
    	instructions.put("UMLALLT", Token.Type.UMLALLT);
    	instructions.put("UMLALGT", Token.Type.UMLALGT);
    	instructions.put("UMLALLE", Token.Type.UMLALLE);
    	instructions.put("UMLALAL", Token.Type.UMLALAL);
    	
    	instructions.put("UMLALS", Token.Type.UMLALS);
    	instructions.put("UMLALEQS", Token.Type.UMLALEQS);
    	instructions.put("UMLALNES", Token.Type.UMLALNES);
    	instructions.put("UMLALCSS", Token.Type.UMLALCSS);
    	instructions.put("UMLALCCS", Token.Type.UMLALCCS);
    	instructions.put("UMLALMIS", Token.Type.UMLALMIS);
    	instructions.put("UMLALPLS", Token.Type.UMLALPLS);
    	instructions.put("UMLALVSS", Token.Type.UMLALVSS);
    	instructions.put("UMLALVCS", Token.Type.UMLALVCS);
    	instructions.put("UMLALHIS", Token.Type.UMLALHIS);
    	instructions.put("UMLALLSS", Token.Type.UMLALLSS);
    	instructions.put("UMLALGES", Token.Type.UMLALGES);
    	instructions.put("UMLALLTS", Token.Type.UMLALLTS);
    	instructions.put("UMLALGTS", Token.Type.UMLALGTS);
    	instructions.put("UMLALLES", Token.Type.UMLALLES);
    	instructions.put("UMLALALS", Token.Type.UMLALALS);
    	
    	instructions.put("SMLAL", Token.Type.SMLAL);
    	instructions.put("SMLALEQ", Token.Type.SMLALEQ);
    	instructions.put("SMLALNE", Token.Type.SMLALNE);
    	instructions.put("SMLALCS", Token.Type.SMLALCS);
    	instructions.put("SMLALCC", Token.Type.SMLALCC);
    	instructions.put("SMLALMI", Token.Type.SMLALMI);
    	instructions.put("SMLALPL", Token.Type.SMLALPL);
    	instructions.put("SMLALVS", Token.Type.SMLALVS);
    	instructions.put("SMLALVC", Token.Type.SMLALVC);
    	instructions.put("SMLALHI", Token.Type.SMLALHI);
    	instructions.put("SMLALLS", Token.Type.SMLALLS);
    	instructions.put("SMLALGE", Token.Type.SMLALGE);
    	instructions.put("SMLALLT", Token.Type.SMLALLT);
    	instructions.put("SMLALGT", Token.Type.SMLALGT);
    	instructions.put("SMLALLE", Token.Type.SMLALLE);
    	instructions.put("SMLALAL", Token.Type.SMLALAL);
    	
    	instructions.put("SMLALS", Token.Type.SMLALS);
    	instructions.put("SMLALEQS", Token.Type.SMLALEQS);
    	instructions.put("SMLALNES", Token.Type.SMLALNES);
    	instructions.put("SMLALCSS", Token.Type.SMLALCSS);
    	instructions.put("SMLALCCS", Token.Type.SMLALCCS);
    	instructions.put("SMLALMIS", Token.Type.SMLALMIS);
    	instructions.put("SMLALPLS", Token.Type.SMLALPLS);
    	instructions.put("SMLALVSS", Token.Type.SMLALVSS);
    	instructions.put("SMLALVCS", Token.Type.SMLALVCS);
    	instructions.put("SMLALHIS", Token.Type.SMLALHIS);
    	instructions.put("SMLALLSS", Token.Type.SMLALLSS);
    	instructions.put("SMLALGES", Token.Type.SMLALGES);
    	instructions.put("SMLALLTS", Token.Type.SMLALLTS);
    	instructions.put("SMLALGTS", Token.Type.SMLALGTS);
    	instructions.put("SMLALLES", Token.Type.SMLALLES);
    	instructions.put("SMLALALS", Token.Type.SMLALALS);
    	
    	//LDR
    	instructions.put("LDR", Token.Type.LDR);
    	instructions.put("LDREQ", Token.Type.LDREQ);
    	instructions.put("LDRNE", Token.Type.LDRNE);
    	instructions.put("LDRCS", Token.Type.LDRCS);
    	instructions.put("LDRCC", Token.Type.LDRCC);
    	instructions.put("LDRMI", Token.Type.LDRMI);
    	instructions.put("LDRPL", Token.Type.LDRPL);
    	instructions.put("LDRVS", Token.Type.LDRVS);
    	instructions.put("LDRVC", Token.Type.LDRVC);
    	instructions.put("LDRHI", Token.Type.LDRHI);
    	instructions.put("LDRLS", Token.Type.LDRLS);
    	instructions.put("LDRGE", Token.Type.LDRGE);
    	instructions.put("LDRLT", Token.Type.LDRLT);
    	instructions.put("LDRGT", Token.Type.LDRGT);
    	instructions.put("LDRLE", Token.Type.LDRLE);
    	instructions.put("LDRAL", Token.Type.LDRAL);
    	
    	instructions.put("LDRB", Token.Type.LDRB);
    	instructions.put("LDREQB", Token.Type.LDREQB);
    	instructions.put("LDRNEB", Token.Type.LDRNEB);
    	instructions.put("LDRCSB", Token.Type.LDRCSB);
    	instructions.put("LDRCCB", Token.Type.LDRCCB);
    	instructions.put("LDRMIB", Token.Type.LDRMIB);
    	instructions.put("LDRPLB", Token.Type.LDRPLB);
    	instructions.put("LDRVSB", Token.Type.LDRVSB);
    	instructions.put("LDRVCB", Token.Type.LDRVCB);
    	instructions.put("LDRHIB", Token.Type.LDRHIB);
    	instructions.put("LDRLSB", Token.Type.LDRLSB);
    	instructions.put("LDRGEB", Token.Type.LDRGEB);
    	instructions.put("LDRLTB", Token.Type.LDRLTB);
    	instructions.put("LDRGTB", Token.Type.LDRGTB);
    	instructions.put("LDRLEB", Token.Type.LDRLEB);
    	instructions.put("LDRALB", Token.Type.LDRALB);
    	
    	instructions.put("LDRT", Token.Type.LDRT);
    	instructions.put("LDREQT", Token.Type.LDREQT);
    	instructions.put("LDRNET", Token.Type.LDRNET);
    	instructions.put("LDRCST", Token.Type.LDRCST);
    	instructions.put("LDRCCT", Token.Type.LDRCCT);
    	instructions.put("LDRMIT", Token.Type.LDRMIT);
    	instructions.put("LDRPLT", Token.Type.LDRPLT);
    	instructions.put("LDRVST", Token.Type.LDRVST);
    	instructions.put("LDRVCT", Token.Type.LDRVCT);
    	instructions.put("LDRHIT", Token.Type.LDRHIT);
    	instructions.put("LDRLST", Token.Type.LDRLST);
    	instructions.put("LDRGET", Token.Type.LDRGET);
    	instructions.put("LDRLTT", Token.Type.LDRLTT);
    	instructions.put("LDRGTT", Token.Type.LDRGTT);
    	instructions.put("LDRLET", Token.Type.LDRLET);
    	instructions.put("LDRALT", Token.Type.LDRALT);
    	
    	instructions.put("LDRBT", Token.Type.LDRBT);
    	instructions.put("LDREQBT", Token.Type.LDREQBT);
    	instructions.put("LDRNEBT", Token.Type.LDRNEBT);
    	instructions.put("LDRCSBT", Token.Type.LDRCSBT);
    	instructions.put("LDRCCBT", Token.Type.LDRCCBT);
    	instructions.put("LDRMIBT", Token.Type.LDRMIBT);
    	instructions.put("LDRPLBT", Token.Type.LDRPLBT);
    	instructions.put("LDRVSBT", Token.Type.LDRVSBT);
    	instructions.put("LDRVCBT", Token.Type.LDRVCBT);
    	instructions.put("LDRHIBT", Token.Type.LDRHIBT);
    	instructions.put("LDRLSBT", Token.Type.LDRLSBT);
    	instructions.put("LDRGEBT", Token.Type.LDRGEBT);
    	instructions.put("LDRLTBT", Token.Type.LDRLTBT);
    	instructions.put("LDRGTBT", Token.Type.LDRGTBT);
    	instructions.put("LDRLEBT", Token.Type.LDRLEBT);
    	instructions.put("LDRALBT", Token.Type.LDRALBT);
    	
    	//STR
    	instructions.put("STR", Token.Type.STR);
    	instructions.put("STREQ", Token.Type.STREQ);
    	instructions.put("STRNE", Token.Type.STRNE);
    	instructions.put("STRCS", Token.Type.STRCS);
    	instructions.put("STRCC", Token.Type.STRCC);
    	instructions.put("STRMI", Token.Type.STRMI);
    	instructions.put("STRPL", Token.Type.STRPL);
    	instructions.put("STRVS", Token.Type.STRVS);
    	instructions.put("STRVC", Token.Type.STRVC);
    	instructions.put("STRHI", Token.Type.STRHI);
    	instructions.put("STRLS", Token.Type.STRLS);
    	instructions.put("STRGE", Token.Type.STRGE);
    	instructions.put("STRLT", Token.Type.STRLT);
    	instructions.put("STRGT", Token.Type.STRGT);
    	instructions.put("STRLE", Token.Type.STRLE);
    	instructions.put("STRAL", Token.Type.STRAL);
    	
    	instructions.put("STRB", Token.Type.STRB);
    	instructions.put("STREQB", Token.Type.STREQB);
    	instructions.put("STRNEB", Token.Type.STRNEB);
    	instructions.put("STRCSB", Token.Type.STRCSB);
    	instructions.put("STRCCB", Token.Type.STRCCB);
    	instructions.put("STRMIB", Token.Type.STRMIB);
    	instructions.put("STRPLB", Token.Type.STRPLB);
    	instructions.put("STRVSB", Token.Type.STRVSB);
    	instructions.put("STRVCB", Token.Type.STRVCB);
    	instructions.put("STRHIB", Token.Type.STRHIB);
    	instructions.put("STRLSB", Token.Type.STRLSB);
    	instructions.put("STRGEB", Token.Type.STRGEB);
    	instructions.put("STRLTB", Token.Type.STRLTB);
    	instructions.put("STRGTB", Token.Type.STRGTB);
    	instructions.put("STRLEB", Token.Type.STRLEB);
    	instructions.put("STRALB", Token.Type.STRALB);
    	
    	instructions.put("STRT", Token.Type.STRT);
    	instructions.put("STREQT", Token.Type.STREQT);
    	instructions.put("STRNET", Token.Type.STRNET);
    	instructions.put("STRCST", Token.Type.STRCST);
    	instructions.put("STRCCT", Token.Type.STRCCT);
    	instructions.put("STRMIT", Token.Type.STRMIT);
    	instructions.put("STRPLT", Token.Type.STRPLT);
    	instructions.put("STRVST", Token.Type.STRVST);
    	instructions.put("STRVCT", Token.Type.STRVCT);
    	instructions.put("STRHIT", Token.Type.STRHIT);
    	instructions.put("STRLST", Token.Type.STRLST);
    	instructions.put("STRGET", Token.Type.STRGET);
    	instructions.put("STRLTT", Token.Type.STRLTT);
    	instructions.put("STRGTT", Token.Type.STRGTT);
    	instructions.put("STRLET", Token.Type.STRLET);
    	instructions.put("STRALT", Token.Type.STRALT);
    	
    	instructions.put("STRBT", Token.Type.STRBT);
    	instructions.put("STREQBT", Token.Type.STREQBT);
    	instructions.put("STRNEBT", Token.Type.STRNEBT);
    	instructions.put("STRCSBT", Token.Type.STRCSBT);
    	instructions.put("STRCCBT", Token.Type.STRCCBT);
    	instructions.put("STRMIBT", Token.Type.STRMIBT);
    	instructions.put("STRPLBT", Token.Type.STRPLBT);
    	instructions.put("STRVSBT", Token.Type.STRVSBT);
    	instructions.put("STRVCBT", Token.Type.STRVCBT);
    	instructions.put("STRHIBT", Token.Type.STRHIBT);
    	instructions.put("STRLSBT", Token.Type.STRLSBT);
    	instructions.put("STRGEBT", Token.Type.STRGEBT);
    	instructions.put("STRLTBT", Token.Type.STRLTBT);
    	instructions.put("STRGTBT", Token.Type.STRGTBT);
    	instructions.put("STRLEBT", Token.Type.STRLEBT);
    	instructions.put("STRALBT", Token.Type.STRALBT);
    	
    	//LDRH
    	instructions.put("LDRH", Token.Type.LDRH);
    	instructions.put("LDREQH", Token.Type.LDREQH);
    	instructions.put("LDRNEH", Token.Type.LDRNEH);
    	instructions.put("LDRCSH", Token.Type.LDRCSH);
    	instructions.put("LDRCCH", Token.Type.LDRCCH);
    	instructions.put("LDRMIH", Token.Type.LDRMIH);
    	instructions.put("LDRPLH", Token.Type.LDRPLH);
    	instructions.put("LDRVSH", Token.Type.LDRVSH);
    	instructions.put("LDRVCH", Token.Type.LDRVCH);
    	instructions.put("LDRHIH", Token.Type.LDRHIH);
    	instructions.put("LDRLSH", Token.Type.LDRLSH);
    	instructions.put("LDRGEH", Token.Type.LDRGEH);
    	instructions.put("LDRLTH", Token.Type.LDRLTH);
    	instructions.put("LDRGTH", Token.Type.LDRGTH);
    	instructions.put("LDRLEH", Token.Type.LDRLEH);
    	instructions.put("LDRAlH", Token.Type.LDRALH);
    	
    	instructions.put("LDRSH", Token.Type.LDRSH);
    	instructions.put("LDREQSH", Token.Type.LDREQSH);
    	instructions.put("LDRNESH", Token.Type.LDRNESH);
    	instructions.put("LDRCSSH", Token.Type.LDRCSSH);
    	instructions.put("LDRCCSH", Token.Type.LDRCCSH);
    	instructions.put("LDRMISH", Token.Type.LDRMISH);
    	instructions.put("LDRPLSH", Token.Type.LDRPLSH);
    	instructions.put("LDRVSSH", Token.Type.LDRVSSH);
    	instructions.put("LDRVCSH", Token.Type.LDRVCSH);
    	instructions.put("LDRHISH", Token.Type.LDRHISH);
    	instructions.put("LDRLSSH", Token.Type.LDRLSSH);
    	instructions.put("LDRGESH", Token.Type.LDRGESH);
    	instructions.put("LDRLTSH", Token.Type.LDRLTSH);
    	instructions.put("LDRGTSH", Token.Type.LDRGTSH);
    	instructions.put("LDRLESH", Token.Type.LDRLESH);
    	instructions.put("LDRAlSH", Token.Type.LDRALSH);
    	
    	instructions.put("LDRSB", Token.Type.LDRSB);
    	instructions.put("LDREQSB", Token.Type.LDREQSB);
    	instructions.put("LDRNESB", Token.Type.LDRNESB);
    	instructions.put("LDRCSSB", Token.Type.LDRCSSB);
    	instructions.put("LDRCCSB", Token.Type.LDRCCSB);
    	instructions.put("LDRMISB", Token.Type.LDRMISB);
    	instructions.put("LDRPLSB", Token.Type.LDRPLSB);
    	instructions.put("LDRVSSB", Token.Type.LDRVSSB);
    	instructions.put("LDRVCSB", Token.Type.LDRVCSB);
    	instructions.put("LDRHISB", Token.Type.LDRHISB);
    	instructions.put("LDRLSSB", Token.Type.LDRLSSB);
    	instructions.put("LDRGESB", Token.Type.LDRGESB);
    	instructions.put("LDRLTSB", Token.Type.LDRLTSB);
    	instructions.put("LDRGTSB", Token.Type.LDRGTSB);
    	instructions.put("LDRLESB", Token.Type.LDRLESB);
    	instructions.put("LDRALSB", Token.Type.LDRALSB);
    	
    	//STRH
    	instructions.put("STRH", Token.Type.STRH);
    	instructions.put("STREQH", Token.Type.STREQH);
    	instructions.put("STRNEH", Token.Type.STRNEH);
    	instructions.put("STRCSH", Token.Type.STRCSH);
    	instructions.put("STRCCH", Token.Type.STRCCH);
    	instructions.put("STRMIH", Token.Type.STRMIH);
    	instructions.put("STRPLH", Token.Type.STRPLH);
    	instructions.put("STRVSH", Token.Type.STRVSH);
    	instructions.put("STRVCH", Token.Type.STRVCH);
    	instructions.put("STRHIH", Token.Type.STRHIH);
    	instructions.put("STRLSH", Token.Type.STRLSH);
    	instructions.put("STRGEH", Token.Type.STRGEH);
    	instructions.put("STRLTH", Token.Type.STRLTH);
    	instructions.put("STRGTH", Token.Type.STRGTH);
    	instructions.put("STRLEH", Token.Type.STRLEH);
    	instructions.put("STRALH", Token.Type.STRALH);
    	
    	instructions.put("STRSH", Token.Type.STRSH);
    	instructions.put("STREQSH", Token.Type.STREQSH);
    	instructions.put("STRNESH", Token.Type.STRNESH);
    	instructions.put("STRCSSH", Token.Type.STRCSSH);
    	instructions.put("STRCCSH", Token.Type.STRCCSH);
    	instructions.put("STRMISH", Token.Type.STRMISH);
    	instructions.put("STRPLSH", Token.Type.STRPLSH);
    	instructions.put("STRVSSH", Token.Type.STRVSSH);
    	instructions.put("STRVCSH", Token.Type.STRVCSH);
    	instructions.put("STRHISH", Token.Type.STRHISH);
    	instructions.put("STRLSSH", Token.Type.STRLSSH);
    	instructions.put("STRGESH", Token.Type.STRGESH);
    	instructions.put("STRLTSH", Token.Type.STRLTSH);
    	instructions.put("STRGTSH", Token.Type.STRGTSH);
    	instructions.put("STRLESH", Token.Type.STRLESH);
    	instructions.put("STRALSH", Token.Type.STRALSH);
    	
    	instructions.put("STRSB", Token.Type.STRSB);
    	instructions.put("STREQSB", Token.Type.STREQSB);
    	instructions.put("STRNESB", Token.Type.STRNESB);
    	instructions.put("STRCSSB", Token.Type.STRCSSB);
    	instructions.put("STRCCSB", Token.Type.STRCCSB);
    	instructions.put("STRMISB", Token.Type.STRMISB);
    	instructions.put("STRPLSB", Token.Type.STRPLSB);
    	instructions.put("STRVSSB", Token.Type.STRVSSB);
    	instructions.put("STRVCSB", Token.Type.STRVCSB);
    	instructions.put("STRHISB", Token.Type.STRHISB);
    	instructions.put("STRLSSB", Token.Type.STRLSSB);
    	instructions.put("STRGESB", Token.Type.STRGESB);
    	instructions.put("STRLTSB", Token.Type.STRLTSB);
    	instructions.put("STRGTSB", Token.Type.STRGTSB);
    	instructions.put("STRLESB", Token.Type.STRLESB);
    	instructions.put("STRALSB", Token.Type.STRALSB);
    	
    	//LDM
    	instructions.put("LDMFD", Token.Type.LDMFD);
    	instructions.put("LDMEQFD", Token.Type.LDMEQFD);
    	instructions.put("LDMNEFD", Token.Type.LDMNEFD);
    	instructions.put("LDMCSFD", Token.Type.LDMCSFD);
    	instructions.put("LDMCCFD", Token.Type.LDMCCFD);
    	instructions.put("LDMMIFD", Token.Type.LDMMIFD);
    	instructions.put("LDMPLFD", Token.Type.LDMPLFD);
    	instructions.put("LDMVSFD", Token.Type.LDMVSFD);
    	instructions.put("LDMVCFD", Token.Type.LDMVCFD);
    	instructions.put("LDMHIFD", Token.Type.LDMHIFD);
    	instructions.put("LDMLSFD", Token.Type.LDMLSFD);
    	instructions.put("LDMGEFD", Token.Type.LDMGEFD);
    	instructions.put("LDMLTFD", Token.Type.LDMLTFD);
    	instructions.put("LDMGTFD", Token.Type.LDMGTFD);
    	instructions.put("LDMLEFD", Token.Type.LDMLEFD);
    	instructions.put("LDMALFD", Token.Type.LDMALFD);
    	
    	instructions.put("LDMFD", Token.Type.LDMFD);
    	instructions.put("LDMEQFD", Token.Type.LDMEQFD);
    	instructions.put("LDMNEFD", Token.Type.LDMNEFD);
    	instructions.put("LDMCSFD", Token.Type.LDMCSFD);
    	instructions.put("LDMCCFD", Token.Type.LDMCCFD);
    	instructions.put("LDMMIFD", Token.Type.LDMMIFD);
    	instructions.put("LDMPLFD", Token.Type.LDMPLFD);
    	instructions.put("LDMVSFD", Token.Type.LDMVSFD);
    	instructions.put("LDMVCFD", Token.Type.LDMVCFD);
    	instructions.put("LDMHIFD", Token.Type.LDMHIFD);
    	instructions.put("LDMLSFD", Token.Type.LDMLSFD);
    	instructions.put("LDMGEFD", Token.Type.LDMGEFD);
    	instructions.put("LDMLTFD", Token.Type.LDMLTFD);
    	instructions.put("LDMGTFD", Token.Type.LDMGTFD);
    	instructions.put("LDMLEFD", Token.Type.LDMLEFD);
    	instructions.put("LDMALFD", Token.Type.LDMALFD);
    	
    	instructions.put("LDMED", Token.Type.LDMED);
    	instructions.put("LDMEQED", Token.Type.LDMEQED);
    	instructions.put("LDMNEED", Token.Type.LDMNEED);
    	instructions.put("LDMCSED", Token.Type.LDMCSED);
    	instructions.put("LDMCCED", Token.Type.LDMCCED);
    	instructions.put("LDMMIED", Token.Type.LDMMIED);
    	instructions.put("LDMPLED", Token.Type.LDMPLED);
    	instructions.put("LDMVSED", Token.Type.LDMVSED);
    	instructions.put("LDMVCED", Token.Type.LDMVCED);
    	instructions.put("LDMHIED", Token.Type.LDMHIED);
    	instructions.put("LDMLSED", Token.Type.LDMLSED);
    	instructions.put("LDMGEED", Token.Type.LDMGEED);
    	instructions.put("LDMLTED", Token.Type.LDMLTED);
    	instructions.put("LDMGTED", Token.Type.LDMGTED);
    	instructions.put("LDMLEED", Token.Type.LDMLEED);
    	instructions.put("LDMALED", Token.Type.LDMALED);
    	
    	instructions.put("LDMFA", Token.Type.LDMFA);
    	instructions.put("LDMEQFA", Token.Type.LDMEQFA);
    	instructions.put("LDMNEFA", Token.Type.LDMNEFA);
    	instructions.put("LDMCSFA", Token.Type.LDMCSFA);
    	instructions.put("LDMCCFA", Token.Type.LDMCCFA);
    	instructions.put("LDMMIFA", Token.Type.LDMMIFA);
    	instructions.put("LDMPLFA", Token.Type.LDMPLFA);
    	instructions.put("LDMVSFA", Token.Type.LDMVSFA);
    	instructions.put("LDMVCFA", Token.Type.LDMVCFA);
    	instructions.put("LDMHIFA", Token.Type.LDMHIFA);
    	instructions.put("LDMLSFA", Token.Type.LDMLSFA);
    	instructions.put("LDMGEFA", Token.Type.LDMGEFA);
    	instructions.put("LDMLTFA", Token.Type.LDMLTFA);
    	instructions.put("LDMGTFA", Token.Type.LDMGTFA);
    	instructions.put("LDMLEFA", Token.Type.LDMLEFA);
    	instructions.put("LDMALFA", Token.Type.LDMALFA);
    	
    	instructions.put("LDMEA", Token.Type.LDMEA);
    	instructions.put("LDMEQEA", Token.Type.LDMEQEA);
    	instructions.put("LDMNEEA", Token.Type.LDMNEEA);
    	instructions.put("LDMCSEA", Token.Type.LDMCSEA);
    	instructions.put("LDMCCEA", Token.Type.LDMCCEA);
    	instructions.put("LDMMIEA", Token.Type.LDMMIEA);
    	instructions.put("LDMPLEA", Token.Type.LDMPLEA);
    	instructions.put("LDMVSEA", Token.Type.LDMVSEA);
    	instructions.put("LDMVCEA", Token.Type.LDMVCEA);
    	instructions.put("LDMHIEA", Token.Type.LDMHIEA);
    	instructions.put("LDMLSEA", Token.Type.LDMLSEA);
    	instructions.put("LDMGEEA", Token.Type.LDMGEEA);
    	instructions.put("LDMLTEA", Token.Type.LDMLTEA);
    	instructions.put("LDMGTEA", Token.Type.LDMGTEA);
    	instructions.put("LDMLEEA", Token.Type.LDMLEEA);
    	instructions.put("LDMALEA", Token.Type.LDMALEA);
    	
    	instructions.put("LDMIA", Token.Type.LDMIA);
    	instructions.put("LDMEQIA", Token.Type.LDMEQIA);
    	instructions.put("LDMNEIA", Token.Type.LDMNEIA);
    	instructions.put("LDMCSIA", Token.Type.LDMCSIA);
    	instructions.put("LDMCCIA", Token.Type.LDMCCIA);
    	instructions.put("LDMMIIA", Token.Type.LDMMIIA);
    	instructions.put("LDMPLIA", Token.Type.LDMPLIA);
    	instructions.put("LDMVSIA", Token.Type.LDMVSIA);
    	instructions.put("LDMVCIA", Token.Type.LDMVCIA);
    	instructions.put("LDMHIIA", Token.Type.LDMHIIA);
    	instructions.put("LDMLSIA", Token.Type.LDMLSIA);
    	instructions.put("LDMGEIA", Token.Type.LDMGEIA);
    	instructions.put("LDMLTIA", Token.Type.LDMLTIA);
    	instructions.put("LDMGTIA", Token.Type.LDMGTIA);
    	instructions.put("LDMLEIA", Token.Type.LDMLEIA);
    	instructions.put("LDMALIA", Token.Type.LDMALIA);
    	
    	instructions.put("LDMDA", Token.Type.LDMDA);
    	instructions.put("LDMEQDA", Token.Type.LDMEQDA);
    	instructions.put("LDMNEDA", Token.Type.LDMNEDA);
    	instructions.put("LDMCSDA", Token.Type.LDMCSDA);
    	instructions.put("LDMCCDA", Token.Type.LDMCCDA);
    	instructions.put("LDMMIDA", Token.Type.LDMMIDA);
    	instructions.put("LDMPLDA", Token.Type.LDMPLDA);
    	instructions.put("LDMVSDA", Token.Type.LDMVSDA);
    	instructions.put("LDMVCDA", Token.Type.LDMVCDA);
    	instructions.put("LDMHIDA", Token.Type.LDMHIDA);
    	instructions.put("LDMLSDA", Token.Type.LDMLSDA);
    	instructions.put("LDMGEDA", Token.Type.LDMGEDA);
    	instructions.put("LDMLTDA", Token.Type.LDMLTDA);
    	instructions.put("LDMGTDA", Token.Type.LDMGTDA);
    	instructions.put("LDMLEDA", Token.Type.LDMLEDA);
    	instructions.put("LDMALDA", Token.Type.LDMALDA);
    	
    	instructions.put("LDMIB", Token.Type.LDMIB);
    	instructions.put("LDMEQIB", Token.Type.LDMEQIB);
    	instructions.put("LDMNEIB", Token.Type.LDMNEIB);
    	instructions.put("LDMCSIB", Token.Type.LDMCSIB);
    	instructions.put("LDMCCIB", Token.Type.LDMCCIB);
    	instructions.put("LDMMIIB", Token.Type.LDMMIIB);
    	instructions.put("LDMPLIB", Token.Type.LDMPLIB);
    	instructions.put("LDMVSIB", Token.Type.LDMVSIB);
    	instructions.put("LDMVCIB", Token.Type.LDMVCIB);
    	instructions.put("LDMHIIB", Token.Type.LDMHIIB);
    	instructions.put("LDMLSIB", Token.Type.LDMLSIB);
    	instructions.put("LDMGEIB", Token.Type.LDMGEIB);
    	instructions.put("LDMLTIB", Token.Type.LDMLTIB);
    	instructions.put("LDMGTIB", Token.Type.LDMGTIB);
    	instructions.put("LDMLEIB", Token.Type.LDMLEIB);
    	instructions.put("LDMALIB", Token.Type.LDMALIB);
    	
    	instructions.put("LDMDB", Token.Type.LDMDB);
    	instructions.put("LDMEQDB", Token.Type.LDMEQDB);
    	instructions.put("LDMNEDB", Token.Type.LDMNEDB);
    	instructions.put("LDMCSDB", Token.Type.LDMCSDB);
    	instructions.put("LDMCCDB", Token.Type.LDMCCDB);
    	instructions.put("LDMMIDB", Token.Type.LDMMIDB);
    	instructions.put("LDMPLDB", Token.Type.LDMPLDB);
    	instructions.put("LDMVSDB", Token.Type.LDMVSDB);
    	instructions.put("LDMVCDB", Token.Type.LDMVCDB);
    	instructions.put("LDMHIDB", Token.Type.LDMHIDB);
    	instructions.put("LDMLSDB", Token.Type.LDMLSDB);
    	instructions.put("LDMGEDB", Token.Type.LDMGEDB);
    	instructions.put("LDMLTDB", Token.Type.LDMLTDB);
    	instructions.put("LDMGTDB", Token.Type.LDMGTDB);
    	instructions.put("LDMLEDB", Token.Type.LDMLEDB);
    	instructions.put("LDMALDB", Token.Type.LDMALDB);
    	
    	//STM
    	instructions.put("STMFD", Token.Type.STMFD);
    	instructions.put("STMEQFD", Token.Type.STMEQFD);
    	instructions.put("STMNEFD", Token.Type.STMNEFD);
    	instructions.put("STMCSFD", Token.Type.STMCSFD);
    	instructions.put("STMCCFD", Token.Type.STMCCFD);
    	instructions.put("STMMIFD", Token.Type.STMMIFD);
    	instructions.put("STMPLFD", Token.Type.STMPLFD);
    	instructions.put("STMVSFD", Token.Type.STMVSFD);
    	instructions.put("STMVCFD", Token.Type.STMVCFD);
    	instructions.put("STMMHFD", Token.Type.STMHIFD);
    	instructions.put("STMMLFD", Token.Type.STMLSFD);
    	instructions.put("STMGEFD", Token.Type.STMGEFD);
    	instructions.put("STMMLFD", Token.Type.STMLTFD);
    	instructions.put("STMGTFD", Token.Type.STMGTFD);
    	instructions.put("STMLEFD", Token.Type.STMLEFD);
    	instructions.put("STMALFD", Token.Type.STMALFD);
    	
    	instructions.put("STMFA", Token.Type.STMFA);
    	instructions.put("STMEQFA", Token.Type.STMEQFA);
    	instructions.put("STMNEFA", Token.Type.STMNEFA);
    	instructions.put("STMCSFA", Token.Type.STMCSFA);
    	instructions.put("STMCCFA", Token.Type.STMCCFA);
    	instructions.put("STMMIFA", Token.Type.STMMIFA);
    	instructions.put("STMPLFA", Token.Type.STMPLFA);
    	instructions.put("STMVSFA", Token.Type.STMVSFA);
    	instructions.put("STMVCFA", Token.Type.STMVCFA);
    	instructions.put("STMMHFA", Token.Type.STMHIFA);
    	instructions.put("STMMLFA", Token.Type.STMLSFA);
    	instructions.put("STMGEFA", Token.Type.STMGEFA);
    	instructions.put("STMMLFA", Token.Type.STMLTFA);
    	instructions.put("STMGTFA", Token.Type.STMGTFA);
    	instructions.put("STMLEFA", Token.Type.STMLEFA);
    	instructions.put("STMALFA", Token.Type.STMALFA);
    	
    	instructions.put("STMED", Token.Type.STMED);
    	instructions.put("STMEQED", Token.Type.STMEQED);
    	instructions.put("STMNEED", Token.Type.STMNEED);
    	instructions.put("STMCSED", Token.Type.STMCSED);
    	instructions.put("STMCCED", Token.Type.STMCCED);
    	instructions.put("STMMIED", Token.Type.STMMIED);
    	instructions.put("STMPLED", Token.Type.STMPLED);
    	instructions.put("STMVSED", Token.Type.STMVSED);
    	instructions.put("STMVCED", Token.Type.STMVCED);
    	instructions.put("STMMHED", Token.Type.STMHIED);
    	instructions.put("STMMLED", Token.Type.STMLSED);
    	instructions.put("STMGEED", Token.Type.STMGEED);
    	instructions.put("STMMLED", Token.Type.STMLTED);
    	instructions.put("STMGTED", Token.Type.STMGTED);
    	instructions.put("STMLEED", Token.Type.STMLEED);
    	instructions.put("STMALED", Token.Type.STMALED);
    	
    	instructions.put("STMEA", Token.Type.STMEA);
    	instructions.put("STMEQEA", Token.Type.STMEQEA);
    	instructions.put("STMNEEA", Token.Type.STMNEEA);
    	instructions.put("STMCSEA", Token.Type.STMCSEA);
    	instructions.put("STMCCEA", Token.Type.STMCCEA);
    	instructions.put("STMMIEA", Token.Type.STMMIEA);
    	instructions.put("STMPLEA", Token.Type.STMPLEA);
    	instructions.put("STMVSEA", Token.Type.STMVSEA);
    	instructions.put("STMVCEA", Token.Type.STMVCEA);
    	instructions.put("STMMHEA", Token.Type.STMHIEA);
    	instructions.put("STMMLEA", Token.Type.STMLSEA);
    	instructions.put("STMGEEA", Token.Type.STMGEEA);
    	instructions.put("STMMLEA", Token.Type.STMLTEA);
    	instructions.put("STMGTEA", Token.Type.STMGTEA);
    	instructions.put("STMLEEA", Token.Type.STMLEEA);
    	instructions.put("STMALEA", Token.Type.STMALEA);
    	
    	instructions.put("STMIA", Token.Type.STMIA);
    	instructions.put("STMEQIA", Token.Type.STMEQIA);
    	instructions.put("STMNEIA", Token.Type.STMNEIA);
    	instructions.put("STMCSIA", Token.Type.STMCSIA);
    	instructions.put("STMCCIA", Token.Type.STMCCIA);
    	instructions.put("STMMIIA", Token.Type.STMMIIA);
    	instructions.put("STMPLIA", Token.Type.STMPLIA);
    	instructions.put("STMVSIA", Token.Type.STMVSIA);
    	instructions.put("STMVCIA", Token.Type.STMVCIA);
    	instructions.put("STMMHIA", Token.Type.STMHIIA);
    	instructions.put("STMMLIA", Token.Type.STMLSIA);
    	instructions.put("STMGEIA", Token.Type.STMGEIA);
    	instructions.put("STMMLIA", Token.Type.STMLTIA);
    	instructions.put("STMGTIA", Token.Type.STMGTIA);
    	instructions.put("STMLEIA", Token.Type.STMLEIA);
    	instructions.put("STMALIA", Token.Type.STMALIA);
    	
    	instructions.put("STMDA", Token.Type.STMDA);
    	instructions.put("STMEQDA", Token.Type.STMEQDA);
    	instructions.put("STMNEDA", Token.Type.STMNEDA);
    	instructions.put("STMCSDA", Token.Type.STMCSDA);
    	instructions.put("STMCCDA", Token.Type.STMCCDA);
    	instructions.put("STMMIDA", Token.Type.STMMIDA);
    	instructions.put("STMPLDA", Token.Type.STMPLDA);
    	instructions.put("STMVSDA", Token.Type.STMVSDA);
    	instructions.put("STMVCDA", Token.Type.STMVCDA);
    	instructions.put("STMMHDA", Token.Type.STMHIDA);
    	instructions.put("STMMLDA", Token.Type.STMLSDA);
    	instructions.put("STMGEDA", Token.Type.STMGEDA);
    	instructions.put("STMMLDA", Token.Type.STMLTDA);
    	instructions.put("STMGTDA", Token.Type.STMGTDA);
    	instructions.put("STMLEDA", Token.Type.STMLEDA);
    	instructions.put("STMALDA", Token.Type.STMALDA);
    	
    	instructions.put("STMIB", Token.Type.STMIB);
    	instructions.put("STMEQIB", Token.Type.STMEQIB);
    	instructions.put("STMNEIB", Token.Type.STMNEIB);
    	instructions.put("STMCSIB", Token.Type.STMCSIB);
    	instructions.put("STMCCIB", Token.Type.STMCCIB);
    	instructions.put("STMMIIB", Token.Type.STMMIIB);
    	instructions.put("STMPLIB", Token.Type.STMPLIB);
    	instructions.put("STMVSIB", Token.Type.STMVSIB);
    	instructions.put("STMVCIB", Token.Type.STMVCIB);
    	instructions.put("STMMHIB", Token.Type.STMHIIB);
    	instructions.put("STMMLIB", Token.Type.STMLSIB);
    	instructions.put("STMGEIB", Token.Type.STMGEIB);
    	instructions.put("STMMLIB", Token.Type.STMLTIB);
    	instructions.put("STMGTIB", Token.Type.STMGTIB);
    	instructions.put("STMLEIB", Token.Type.STMLEIB);
    	instructions.put("STMALIB", Token.Type.STMALIB);
    	
    	instructions.put("STMDB", Token.Type.STMDB);
    	instructions.put("STMEQDB", Token.Type.STMEQDB);
    	instructions.put("STMNEDB", Token.Type.STMNEDB);
    	instructions.put("STMCSDB", Token.Type.STMCSDB);
    	instructions.put("STMCCDB", Token.Type.STMCCDB);
    	instructions.put("STMMIDB", Token.Type.STMMIDB);
    	instructions.put("STMPLDB", Token.Type.STMPLDB);
    	instructions.put("STMVSDB", Token.Type.STMVSDB);
    	instructions.put("STMVCDB", Token.Type.STMVCDB);
    	instructions.put("STMHIDB", Token.Type.STMHIDB);
    	instructions.put("STMMLDB", Token.Type.STMLSDB);
    	instructions.put("STMGEDB", Token.Type.STMGEDB);
    	instructions.put("STMMLDB", Token.Type.STMLTDB);
    	instructions.put("STMGTDB", Token.Type.STMGTDB);
    	instructions.put("STMLEDB", Token.Type.STMLEDB);
    	instructions.put("STMALDB", Token.Type.STMALDB);
    	
    	//STW
    	instructions.put("STW", Token.Type.STW);
    	instructions.put("STWEQ", Token.Type.STWEQ);
    	instructions.put("STWNE", Token.Type.STWNE);
    	instructions.put("STWCS", Token.Type.STWCS);
    	instructions.put("STWCC", Token.Type.STWCC);
    	instructions.put("STWMI", Token.Type.STWMI);
    	instructions.put("STWPL", Token.Type.STWPL);
    	instructions.put("STWVS", Token.Type.STWVS);
    	instructions.put("STWVC", Token.Type.STWVC);
    	instructions.put("STWHI", Token.Type.STWHI);
    	instructions.put("STWLS", Token.Type.STWLS);
    	instructions.put("STWGE", Token.Type.STWGE);
    	instructions.put("STWLT", Token.Type.STWLT);
    	instructions.put("STWGT", Token.Type.STWGT);
    	instructions.put("STWLE", Token.Type.STWLE);
    	instructions.put("STWAL", Token.Type.STWAL);
    	
    	instructions.put("STWB", Token.Type.STWB);
    	instructions.put("STWEQB", Token.Type.STWEQB);
    	instructions.put("STWNEB", Token.Type.STWNEB);
    	instructions.put("STWCSB", Token.Type.STWCSB);
    	instructions.put("STWCCB", Token.Type.STWCCB);
    	instructions.put("STWMIB", Token.Type.STWMIB);
    	instructions.put("STWPLB", Token.Type.STWPLB);
    	instructions.put("STWVSB", Token.Type.STWVSB);
    	instructions.put("STWVCB", Token.Type.STWVCB);
    	instructions.put("STWHIB", Token.Type.STWHIB);
    	instructions.put("STWLSB", Token.Type.STWLSB);
    	instructions.put("STWGEB", Token.Type.STWGEB);
    	instructions.put("STWLTB", Token.Type.STWLTB);
    	instructions.put("STWGTB", Token.Type.STWGTB);
    	instructions.put("STWLEB", Token.Type.STWLEB);
    	instructions.put("STWALB", Token.Type.STWALB);
    	
    	//SWI
    	instructions.put("SWI", Token.Type.SWI);
    	instructions.put("SWIEQ", Token.Type.SWIEQ);
    	instructions.put("SWINE", Token.Type.SWINE);
    	instructions.put("SWICS", Token.Type.SWICS);
    	instructions.put("SWICC", Token.Type.SWICC);
    	instructions.put("SWIMI", Token.Type.SWIMI);
    	instructions.put("SWIPL", Token.Type.SWIPL);
    	instructions.put("SWIVS", Token.Type.SWIVS);
    	instructions.put("SWIVC", Token.Type.SWIVC);
    	instructions.put("SWIHI", Token.Type.SWIHI);
    	instructions.put("SWILS", Token.Type.SWILS);
    	instructions.put("SWIGE", Token.Type.SWIGE);
    	instructions.put("SWILT", Token.Type.SWILT);
    	instructions.put("SWIGT", Token.Type.SWIGT);
    	instructions.put("SWILE", Token.Type.SWILE);
    	instructions.put("SWIAL", Token.Type.SWIAL);
    	
    	//Stop
    	instructions.put("STOP", Token.Type.STOP);
    }

    private final Token.Type type;
    private final String lexeme;
    private final Position position;
    
    private Token(Token.Type type, String lexeme, Position position){
		this.type = type;
		this.lexeme = lexeme;
		this.position = position;
    }

    public String getLexeme(){
    	return lexeme; //ToDo - I will need to complete this later
    }

    public Position getPosition(){
    	return position;
    }

    public static Token makeNumToken(String lexeme, Position pos){
    	return new Token(Token.Type.NUM, lexeme, pos);
    }

    public static Token makeIdentToken(String lexeme, Position pos){
    	if(registers.containsKey(lexeme.toUpperCase())) {
    		return new Token(registers.get(lexeme.toUpperCase()), lexeme, pos);
    	} else if(instructions.containsKey(lexeme.toUpperCase())) {
    		return new Token(instructions.get(lexeme.toUpperCase()), lexeme, pos);
    	} else {
    		return new Token(Token.Type.IDENT, lexeme, pos);
    	}
    }
    
    public static Token makeLabelToken(String lexeme, Position pos) {
		return new Token(Token.Type.LABEL, lexeme, pos);
    }

    public static Token makeStringToken(String lexeme, Position pos){
    	return new Token(Token.Type.STRING, lexeme, pos);
    }

    public static Token makeDirToken(String lexeme, Position pos){
    	return new Token(Token.Type.DIRECTIVE, lexeme, pos);
    }
    
    public static boolean containsOp(String lexeme) {
    	return ops.containsKey(lexeme);
    }
    
    public static Token makeOpToken(String lexeme, Position pos) {
    	return new Token(ops.get(lexeme), lexeme, pos);
    }
    
    public static Token makeCharToken(String lexeme, Position pos) {
    	return new Token(Token.Type.CHAR, lexeme, pos);
    }
    
    public String toString() {
    	return "Token " + this.type + " (" + this.lexeme + ") found " + this.position.toString(); 
    }
}
